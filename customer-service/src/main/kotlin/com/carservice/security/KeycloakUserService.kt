package com.carservice.security

import com.carservice.config.LoggerUtil
import com.carservice.model.profile.Address
import com.carservice.model.profile.Customer
import com.carservice.model.profile.Employee
import com.carservice.model.profile.ProfileType
import com.carservice.model.profile.UserRole
import com.carservice.repository.ProfileRepository
import jakarta.ws.rs.core.Response
import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class KeycloakUserService(
    private val keycloak: Keycloak,
    private val profileRepository: ProfileRepository
) {

    @Value("\${keycloak.realm}")
    private lateinit var realm: String
    private val logger = LoggerUtil.getLogger(KeycloakUserService::class.java)

    @Transactional
    fun createCustomer(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        phoneNumber: String,
        dateOfBirth: LocalDate,
        address: Address,
        roles: Set<UserRole> = setOf(UserRole.GUEST)
    ): String {
        val userId = createKeycloakUser(firstName, lastName, email, password, roles)
        val customer = Customer(
            id = userId,
            firstName = firstName,
            lastName = lastName,
            email = email,
            password = "",
            phoneNumber = phoneNumber,
            dateOfBirth = dateOfBirth,
            address = address,
            role = roles,
            profileType = ProfileType.CUSTOMER,
            vehiclesVin = emptyList(),
            serviceVisitIds = emptyList(),
            visitCounter = 0
        )
        profileRepository.save(customer)
        return userId
    }

    @Transactional
    fun createEmployee(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        phoneNumber: String,
        dateOfBirth: LocalDate,
        address: Address,
        roles: Set<UserRole> = setOf(UserRole.MECHANIC)
    ): String {
        val userId = createKeycloakUser(firstName, lastName, email, password, roles)
        val employee = Employee(
            id = userId,
            firstName = firstName,
            lastName = lastName,
            email = email,
            password = "",
            phoneNumber = phoneNumber,
            dateOfBirth = dateOfBirth,
            address = address,
            role = roles,
            profileType = ProfileType.EMPLOYEE
        )
        profileRepository.save(employee)
        return userId
    }

    private fun createKeycloakUser(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        roles: Set<UserRole>
    ): String {
        val userRep = UserRepresentation().apply {
            username = email
            this.email = email
            this.firstName = firstName
            this.lastName = lastName
            isEnabled = true
        }

        val response: Response = keycloak.realm(realm).users().create(userRep)
        if (response.status != 201) {
            throw RuntimeException("Error creating user in Keycloak: ${response.statusInfo.reasonPhrase}")
        }

        val userId = response.location.path.split("/").last()

        val credential = CredentialRepresentation().apply {
            type = CredentialRepresentation.PASSWORD
            value = password
            isTemporary = false
        }
        keycloak.realm(realm).users().get(userId).resetPassword(credential)

        val roleReps = roles.map { role ->
            keycloak.realm(realm).roles().get(role.name).toRepresentation()
        }
        keycloak.realm(realm).users().get(userId).roles().realmLevel().add(roleReps)

        return userId
    }

    fun updateUserPassword(userId: String, newPassword: String) {
        logger.debug("Attempting to update password for user ID: $userId")
        val users = keycloak.realm(realm).users()
        val user = users.get(userId).toRepresentation()
            ?: throw NoSuchElementException("User with ID $userId not found in Keycloak")

        val credential = CredentialRepresentation().apply {
            type = CredentialRepresentation.PASSWORD
            value = newPassword
            isTemporary = false
        }

        try {
            users.get(user.id).resetPassword(credential)
            logger.info("Password updated successfully for user ID: $userId")
        } catch (e: Exception) {
            logger.error("Failed to update password for user ID $userId: ${e.message}", e)
            throw RuntimeException("Failed to update password: ${e.message}", e)
        }
    }
}