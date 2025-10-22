package com.carservice.security

import com.carservice.config.LoggerUtil
import com.carservice.model.profile.Profile
import com.carservice.model.profile.isAdmin
import com.carservice.model.profile.isEmployeeRole
import com.carservice.repository.ProfileRepository
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
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
        val loggedInEmail = when (principal) {
            is String -> principal
            is org.springframework.security.oauth2.jwt.Jwt -> principal.claims["email"] as? String ?: principal.subject
            else -> throw IllegalStateException("Unsupported principal type: ${principal.javaClass}")
        } ?: throw IllegalStateException("Email not found in authentication principal")

        logger.debug("Searching for requester with email: $loggedInEmail")
        return profileRepository.findByEmail(loggedInEmail)
            ?: throw NoSuchElementException("User with email $loggedInEmail not found")
    }

    fun hasEmployeePrivileges(profile: Profile): Boolean {
        return profile.role.any { it.isEmployeeRole() || it.isAdmin() }
    }

    fun checkEmployeePrivileges() {
        val requester = getRequester()
        if (!hasEmployeePrivileges(requester)) {
            logger.warn("Access denied for user ${requester.email} due to insufficient privileges")
            throw AccessDeniedException("You are not allowed to access this resource.")
        }
    }

    fun isSelf(profile: Profile, targetUserId: String): Boolean {
        return profile.id == targetUserId
    }
}