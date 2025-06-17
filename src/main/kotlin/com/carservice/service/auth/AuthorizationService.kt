package com.carservice.service.auth

import com.carservice.dto.authorization.CustomerSignupRequest
import com.carservice.dto.authorization.EmployeeSignupRequest
import com.carservice.model.profile.*
import com.carservice.repository.ProfileRepository
import com.carservice.validation.customerSignupValidator
import com.carservice.validation.employeeSignUpValidator
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*
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
            id = UUID(randomUUID().mostSignificantBits, randomUUID().leastSignificantBits),
            role = setOf(UserRole.GUEST),
            firstName = request.firstName,
            lastName = request.lastName,
            email = request.email,
            password = passwordEncoder.encode(request.password),
            phoneNumber = request.phoneNumber,
            dateOfBirth = request.dateOfBirth,
            address = request.address,
            serviceVisits = emptyList()
        )
        return profileRepository.save(customer)
    }

    fun signupEmployee(request: EmployeeSignupRequest): Profile {
        val result = employeeSignUpValidator.validate(request)
        if (result.errors.isNotEmpty()) {
            throw IllegalArgumentException("Invalid employee signup request: ${result.errors.joinToString(", ")}")
        }

        if (request.role.any { !(it.isEmployeeRole() || it.isAdmin()) }) {
            throw IllegalArgumentException("Invalid role for employee")
        }

        val employee = Employee(
            id = UUID(randomUUID().mostSignificantBits, randomUUID().leastSignificantBits),
            firstName = request.firstName,
            lastName = request.lastName,
            email = request.email,
            password = passwordEncoder.encode(request.password),
            phoneNumber = request.phoneNumber,
            dateOfBirth = request.dateOfBirth,
            address = request.address,
            role = request.role
        )
        return profileRepository.save(employee)
    }
}