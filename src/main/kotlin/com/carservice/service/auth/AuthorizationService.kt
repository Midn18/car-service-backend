package com.carservice.service.auth

import com.carservice.dto.authorization.CustomerSignupRequest
import com.carservice.dto.authorization.EmployeeSignupRequest
import com.carservice.model.profile.*
import com.carservice.repository.ProfileRepository
import com.carservice.validation.customerSignupValidator
import com.carservice.validation.employeeSignUpValidator
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.UUID.randomUUID

@Service
class AuthorizationService(
    private val profileRepository: ProfileRepository,
    private val passwordEncoder: PasswordEncoder,
) {

    fun signupCustomer(request: CustomerSignupRequest): Profile {
        val result = customerSignupValidator.validate(request)
        if (result.errors.isNotEmpty()) {
            throw IllegalArgumentException("Invalid customer signup request: ${result.errors.joinToString(", ")}")
        }

        val customer = Customer(
            id = randomUUID().toString(),
            role = setOf(UserRole.GUEST),
            firstName = request.firstName,
            lastName = request.lastName,
            email = request.email,
            password = passwordEncoder.encode(request.password),
            phoneNumber = request.phoneNumber,
            dateOfBirth = request.dateOfBirth,
            address = request.address,
            serviceVisitIds = emptyList()
        )
        return profileRepository.save(customer)
    }

    fun signupEmployee(request: EmployeeSignupRequest): Profile {
        val result = employeeSignUpValidator.validate(request)
        val role = request.role.map { UserRole.valueOf(it.name) }.toSet()

        if (role.any { !(it.isEmployeeRole() || it.isAdmin()) }) {
            throw IllegalArgumentException("Invalid role for employee")
        }
        if (result.errors.isNotEmpty()) {
            throw IllegalArgumentException("Invalid employee signup request: ${result.errors.joinToString(", ")}")
        }

        val employee = Employee(
            id = randomUUID().toString(),
            firstName = request.firstName,
            lastName = request.lastName,
            email = request.email,
            password = passwordEncoder.encode(request.password),
            phoneNumber = request.phoneNumber,
            dateOfBirth = request.dateOfBirth,
            address = request.address,
            role = request.role.map { UserRole.valueOf(it.name) }.toSet()
        )
        return profileRepository.save(employee)
    }
}