package com.carservice.service.auth

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

    fun signup(profile: Profile): Profile {

        if (profileRepository.existsByEmail(profile.email)) {
            throw IllegalArgumentException("A user with this email already exists")
        }

        if (profileRepository.existsByPhoneNumber(profile.phoneNumber)) {
            throw IllegalArgumentException("A user with this phone number already exists")
        }

        return when (profile) {
            is Customer -> {
                val result = customerSignupValidator.validate(profile)
                if (result.errors.isNotEmpty()) {
                    throw IllegalArgumentException("Invalid customer signup request: ${result.errors.joinToString(", ")}")
                }

                val customer = profile.copy(
                    id = randomUUID().toString(),
                    password = passwordEncoder.encode(profile.password),
                    role = setOf(UserRole.GUEST),
                    serviceVisitIds = emptyList()
                )
                profileRepository.save(customer)
            }

            is Employee -> {
                val result = employeeSignUpValidator.validate(profile)
                if (result.errors.isNotEmpty()) {
                    throw IllegalArgumentException("Invalid employee signup request: ${result.errors.joinToString(", ")}")
                }

                if (profile.role.any { !(it.isEmployeeRole() || it.isAdmin()) }) {
                    throw IllegalArgumentException("Invalid role for employee")
                }

                val employee = profile.copy(
                    id = randomUUID().toString(),
                    password = passwordEncoder.encode(profile.password),
                    role = profile.role.map { UserRole.valueOf(it.name) }.toSet()
                )
                profileRepository.save(employee)
            }

            else -> throw IllegalArgumentException("Unsupported profile type: ${profile.javaClass.simpleName}")
        }
    }
}