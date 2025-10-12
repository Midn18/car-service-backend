package com.carservice.security

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

    fun getRequester(): Profile {
        val auth = SecurityContextHolder.getContext().authentication
        val loggedInEmail = auth.name

        return profileRepository.findByEmail(loggedInEmail)
            ?: throw NoSuchElementException("User with email $loggedInEmail not found")
    }

    fun hasEmployeePrivileges(profile: Profile): Boolean {
        return profile.role.any { it.isEmployeeRole() || it.isAdmin() }
    }

    fun checkEmployeePrivileges() {
        val requester = getRequester()
        if (!hasEmployeePrivileges(requester)) {
            throw AccessDeniedException("You are not allowed to access this resource.")
        }
    }

    fun isSelf(profile: Profile, targetUserId: String): Boolean {
        return profile.id == targetUserId
    }
}