package com.carservice.security

import com.carservice.config.LoggerUtil
import com.carservice.model.profile.Profile
import com.carservice.model.profile.isAdmin
import com.carservice.model.profile.isEmployeeRole
import com.carservice.repository.ProfileRepository
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component

@Component
class AuthorizationHelper(
    private val profileRepository: ProfileRepository
) {
    private val logger = LoggerUtil.getLogger<AuthorizationHelper>()

    fun getRequester(): Profile {
        val auth = SecurityContextHolder.getContext().authentication
        if (auth == null || !auth.isAuthenticated || auth.principal == "anonymousUser") {
            logger.warn("Authentication context is invalid or anonymous")
            throw AccessDeniedException("User is not authenticated")
        }

        val principal = auth.principal
        return when (principal) {
            is Jwt -> {
                val sub = principal.subject
                    ?: throw IllegalStateException("No subject claim in JWT")

                // Primary: look up by Auth0 user ID (stored as auth0Id field)
                profileRepository.findByAuth0Id(sub)
                    // Fallback: look up by e-mail
                    ?: run {
                        val email = principal.getClaimAsString("email") ?: sub
                        logger.debug("Profile not found by auth0Id, falling back to email: $email")
                        profileRepository.findByEmail(email)
                    }
                    ?: throw NoSuchElementException("Profile not found for Auth0 subject: $sub")
            }
            else -> throw IllegalStateException("Unsupported principal type: ${principal.javaClass}")
        }.also { logger.debug("Requester resolved: ${it.email} (id=${it.id})") }
    }

    fun hasEmployeePrivileges(profile: Profile): Boolean =
        profile.role.any { it.isEmployeeRole() || it.isAdmin() }

    fun checkEmployeePrivileges() {
        val requester = getRequester()
        if (!hasEmployeePrivileges(requester)) {
            logger.warn("Access denied for user ${requester.email} – insufficient privileges")
            throw AccessDeniedException("You are not allowed to access this resource.")
        }
    }

    fun isSelf(profile: Profile, targetUserId: String): Boolean = profile.id == targetUserId
}