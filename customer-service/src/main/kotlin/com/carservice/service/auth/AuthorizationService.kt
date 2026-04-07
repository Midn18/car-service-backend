package com.carservice.service.auth

import com.auth0.client.auth.AuthAPI
import com.carservice.client.EmployeeServiceClient
import com.carservice.dto.LoginResponse
import com.carservice.event.EmployeeCreatedEvent
import com.carservice.model.profile.Customer
import com.carservice.model.profile.Employee
import com.carservice.model.profile.Profile
import com.carservice.model.profile.UserRole
import com.carservice.model.profile.isAdmin
import com.carservice.model.profile.isEmployeeRole
import com.carservice.repository.ProfileRepository
import com.carservice.security.Auth0UserService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthorizationService(
    private val profileRepository: ProfileRepository,
    private val auth0UserService: Auth0UserService,
    private val employeeServiceClient: EmployeeServiceClient,
    private val authAPI: AuthAPI
) {
    private val logger = LoggerFactory.getLogger(AuthorizationService::class.java)

    @Value("\${auth0.audience}")
    private lateinit var audience: String

    fun login(email: String, password: String): LoginResponse {
        logger.debug("Attempting login for: $email")
        return try {
            val tokenHolder = authAPI
                .login(email, password.toCharArray())
                .setRealm("Username-Password-Authentication")
                .setScope("openid offline_access")
                .setAudience(audience)
                .execute()
                .body
            logger.info("Login successful for: $email")
            LoginResponse(
                accessToken = tokenHolder.accessToken,
                refreshToken = tokenHolder.refreshToken ?: tokenHolder.accessToken
            )
        } catch (e: Exception) {
            logger.warn("Login failed for $email: ${e.message}")
            throw RuntimeException("Invalid credentials", e)
        }
    }

    @Transactional
    fun signup(profile: Profile): Profile = when (profile) {
        is Customer -> signupCustomer(profile)
        is Employee -> signupEmployee(profile)
        else -> throw IllegalArgumentException("Unsupported profile type: ${profile.javaClass.simpleName}")
    }

    @Transactional
    fun signupCustomer(customer: Customer): Customer {
        validateUniqueFields(customer.email, customer.phoneNumber)
        val userId = auth0UserService.createCustomer(
            firstName = customer.firstName,
            lastName = customer.lastName,
            email = customer.email,
            password = customer.password,
            phoneNumber = customer.phoneNumber,
            dateOfBirth = customer.dateOfBirth,
            address = customer.address,
            roles = customer.role.ifEmpty { setOf(UserRole.GUEST) }
        )
        return profileRepository.findById(userId).orElseThrow {
            IllegalStateException("Failed to retrieve created customer with ID: $userId")
        } as Customer
    }

    @Transactional
    fun signupEmployee(employee: Employee): Employee {
        validateUniqueFields(employee.email, employee.phoneNumber)
        if (employee.role.any { !(it.isEmployeeRole() || it.isAdmin()) }) {
            throw IllegalArgumentException("Invalid role for employee: only MECHANIC, CAR_DETAILER, CAR_PAINTER, ELECTRICIAN, or ADMIN are allowed")
        }
        val userId = auth0UserService.createEmployee(
            firstName = employee.firstName,
            lastName = employee.lastName,
            email = employee.email,
            password = employee.password,
            phoneNumber = employee.phoneNumber,
            dateOfBirth = employee.dateOfBirth,
            address = employee.address,
            roles = employee.role
        )
        val savedEmployee = profileRepository.findById(userId).orElseThrow {
            IllegalStateException("Failed to retrieve created employee with ID: $userId")
        } as Employee

        employeeServiceClient.initializeSalary(
            EmployeeCreatedEvent(
                employeeId = userId,
                email = savedEmployee.email,
                firstName = savedEmployee.firstName,
                lastName = savedEmployee.lastName,
                roles = savedEmployee.role.map { it.name }.toSet()
            )
        )

        return savedEmployee
    }

    private fun validateUniqueFields(email: String, phoneNumber: String) {
        if (profileRepository.existsByEmail(email))
            throw IllegalArgumentException("A user with this email already exists")
        if (profileRepository.existsByPhoneNumber(phoneNumber))
            throw IllegalArgumentException("A user with this phone number already exists")
    }
}