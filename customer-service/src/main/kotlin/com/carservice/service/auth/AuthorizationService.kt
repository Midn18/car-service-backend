package com.carservice.service.auth

import com.carservice.event.EmployeeCreatedEvent
import com.carservice.model.profile.Customer
import com.carservice.model.profile.Employee
import com.carservice.model.profile.Profile
import com.carservice.model.profile.UserRole
import com.carservice.model.profile.isAdmin
import com.carservice.model.profile.isEmployeeRole
import com.carservice.repository.ProfileRepository
import com.carservice.security.KeycloakUserService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.kafka.core.KafkaTemplate

@Service
class AuthorizationService(
    private val profileRepository: ProfileRepository,
    private val keycloakUserService: KeycloakUserService,
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {

    private val logger = LoggerFactory.getLogger(AuthorizationService::class.java)

    @Transactional
    fun signup(profile: Profile): Profile {
        return when (profile) {
            is Customer -> signupCustomer(profile)
            is Employee -> signupEmployee(profile)
            else -> throw IllegalArgumentException("Unsupported profile type: ${profile.javaClass.simpleName}")
        }
    }

    @Transactional
    fun signupCustomer(customer: Customer): Customer {
        validateUniqueFields(customer.email, customer.phoneNumber)
        val userId = keycloakUserService.createCustomer(
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
        val userId = keycloakUserService.createEmployee(
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

        val event = EmployeeCreatedEvent(
            employeeId = userId,
            email = savedEmployee.email,
            firstName = savedEmployee.firstName,
            lastName = savedEmployee.lastName,
            roles = savedEmployee.role.map { it.name }.toSet()
        )

        kafkaTemplate.send("employee.created", userId, event)
            .whenComplete { result, ex ->
                if (ex == null) {
                    logger.info("EmployeeCreatedEvent sent successfully: $userId")
                } else {
                    logger.error("Failed to send EmployeeCreatedEvent for employee $userId: ${ex.message}", ex)
                }
            }

        return savedEmployee
    }

    private fun validateUniqueFields(email: String, phoneNumber: String) {
        if (profileRepository.existsByEmail(email)) {
            throw IllegalArgumentException("A user with this email already exists")
        }
        if (profileRepository.existsByPhoneNumber(phoneNumber)) {
            throw IllegalArgumentException("A user with this phone number already exists")
        }
    }
}