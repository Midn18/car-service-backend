package com.carservice.security

import com.auth0.client.auth.AuthAPI
import com.auth0.client.mgmt.ManagementAPI
import com.auth0.json.mgmt.users.User
import com.carservice.config.LoggerUtil
import com.carservice.model.profile.Address
import com.carservice.model.profile.Customer
import com.carservice.model.profile.Employee
import com.carservice.model.profile.ProfileType
import com.carservice.model.profile.UserRole
import com.carservice.repository.ProfileRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.UUID

/**
 * Manages users via the Auth0 Management API.
 *
 * Auth0 Dashboard prerequisites:
 *  1. Create an API with identifier = AUTH0_AUDIENCE env var.
 *  2. Create a Machine-to-Machine application and authorize it to the Auth0
 *     Management API with scopes: create:users, update:users, read:users.
 *  3. Enable "Resource Owner Password Grant" for the application (for login).
 *  4. (Optional) Add a Post-Login Action to include roles in the access token:
 *       const ns = 'https://carservice.com';
 *       api.accessToken.setCustomClaim(`${ns}/email`, event.user.email);
 *       api.accessToken.setCustomClaim(`${ns}/roles`, event.user.app_metadata?.roles || []);
 */
@Service
class Auth0UserService(
    private val authAPI: AuthAPI,
    private val profileRepository: ProfileRepository
) {
    @Value("\${auth0.domain}")
    private lateinit var domain: String

    @Value("\${auth0.management-api-audience}")
    private lateinit var managementApiAudience: String

    private val logger = LoggerUtil.getLogger(Auth0UserService::class.java)

    // ── Management API client (fresh M2M token per call) ───────────────────

    private fun managementAPI(): ManagementAPI {
        val token = authAPI.requestToken(managementApiAudience).execute().body.accessToken
        return ManagementAPI.newBuilder(domain, token).build()
    }

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
        val auth0UserId = createAuth0User(firstName, lastName, email, password, roles)
        val profileId = UUID.randomUUID().toString()
        profileRepository.save(
            Customer(
                id = profileId,
                auth0Id = auth0UserId,
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
        )
        return profileId
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
        val auth0UserId = createAuth0User(firstName, lastName, email, password, roles)
        val profileId = UUID.randomUUID().toString()
        profileRepository.save(
            Employee(
                id = profileId,
                auth0Id = auth0UserId,
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
        )
        return profileId
    }

    fun updateUserPassword(userId: String, newPassword: String) {
        logger.debug("Updating password for Auth0 user: $userId")
        val user = User()
        user.setPassword(newPassword.toCharArray())
        try {
            managementAPI().users().update(userId, user).execute()
            logger.info("Password updated for Auth0 user: $userId")
        } catch (e: Exception) {
            logger.error("Failed to update password for user $userId: ${e.message}", e)
            throw RuntimeException("Failed to update password in Auth0: ${e.message}", e)
        }
    }

    // ── Private helpers ────────────────────────────────────────────────────

    private fun createAuth0User(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        roles: Set<UserRole>
    ): String {
        // Pass the connection via the constructor (the field is private in Auth0 SDK 2.x)
        val user = User("Username-Password-Authentication")
        user.email = email
        user.givenName = firstName
        user.familyName = lastName
        user.name = "$firstName $lastName"
        user.isEmailVerified = true
        user.setPassword(password.toCharArray())
        // Roles in app_metadata so the Auth0 Post-Login Action can embed them in the token
        user.appMetadata = mapOf("roles" to roles.map { it.name })
        val createdUser = managementAPI().users().create(user).execute().body
        logger.info("Auth0 user created: ${createdUser.id} ($email)")
        return createdUser.id
    }
}
