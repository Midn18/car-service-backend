package com.carservice.service.auth

import com.carservice.model.profile.Profile
import jakarta.ws.rs.NotFoundException
import jakarta.ws.rs.core.Response
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.admin.client.resource.RealmResource
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.RoleRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class KeycloakUserService(
    @Value("\${keycloak.auth-server-url}") private val keycloakUrl: String,
    @Value("\${keycloak.realm}") private val realm: String,
    @Value("\${keycloak.admin.client-id}") private val adminClientId: String,
    @Value("\${keycloak.admin.client-secret}") private val adminClientSecret: String
) {
    private val logger = LoggerFactory.getLogger(KeycloakUserService::class.java)

    private fun getKeycloakAdmin(): Keycloak {
        logger.info("Authenticating service account: $adminClientId")
        return KeycloakBuilder.builder()
            .serverUrl(keycloakUrl)
            .realm("master")
            .clientId(adminClientId)
            .clientSecret(adminClientSecret)
            .grantType("client_credentials")
            .scope("realm-management")
            .build()
    }

    fun createUser(profile: Profile, rawPassword: String) {
        val keycloak = getKeycloakAdmin()
        val realmResource: RealmResource = keycloak.realm(realm)

        logger.debug("Searching user by email: ${profile.email}")
        try {
            val existingList = realmResource.users().searchByEmail(profile.email, true)  // exact match
            if (existingList.isNotEmpty()) {
                logger.info("User already exists: ${profile.email}")
                return
            }
        } catch (e: Exception) {
            logger.error("Search by email failed for ${profile.email}: ${e.message}", e)
        }

        val userRep = UserRepresentation().apply {
            setUsername(profile.email)
            setEmail(profile.email)
            setFirstName(profile.firstName)
            setLastName(profile.lastName)
            isEnabled = true
            isEmailVerified = true
            setAttributes(mapOf("mongoProfileId" to listOf(profile.id)))
            setCredentials(listOf(CredentialRepresentation().apply {
                type = CredentialRepresentation.PASSWORD
                value = rawPassword
                isTemporary = false
            }))
        }

        val response: Response = realmResource.users().create(userRep)
        if (response.status != 201) {
            val errorBody = response.readEntity(String::class.java) ?: "No error body"
            logger.error("Failed to create Keycloak user: ${response.status} - $errorBody")
            throw RuntimeException("Failed to create Keycloak user: ${response.status} - $errorBody")
        }

        val createdList = realmResource.users().searchByEmail(profile.email, true)
        val createdUser = createdList.firstOrNull() ?: throw RuntimeException("User not found after creation")
        val userId = createdUser.id

        profile.role.forEach { role ->
            val roleName = role.name
            try {
                realmResource.roles().get(roleName)
            } catch (e: NotFoundException) {
                val roleRep = RoleRepresentation().apply { setName(roleName) }
                realmResource.roles().create(roleRep)
            }
            realmResource.users().get(userId).roles().realmLevel().add(
                listOf(RoleRepresentation().apply { setName(roleName) })
            )
        }
        logger.info("User created successfully: ${profile.email}")
    }

    fun updateUserPassword(email: String, newPassword: String) {
        val keycloak = getKeycloakAdmin()
        val realmResource: RealmResource = keycloak.realm(realm)

        val user = realmResource.users().search(email, 0, 1).firstOrNull()
            ?: throw RuntimeException("User with email $email not found")

        val userId = user.id
        val credential = CredentialRepresentation().apply {
            type = CredentialRepresentation.PASSWORD
            value = newPassword
            isTemporary = false
        }

        realmResource.users().get(userId).resetPassword(credential)
    }
}