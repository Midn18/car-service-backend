package com.carservice.service

import com.carservice.dto.profile.CustomerProfileResponse
import com.carservice.mapper.profile.CustomerMapper
import com.carservice.model.profile.Customer
import com.carservice.model.profile.Profile
import com.carservice.model.profile.ProfileType
import com.carservice.model.profile.isAdmin
import com.carservice.model.profile.isEmployeeRole
import com.carservice.repository.ProfileRepository
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.util.*

@Service
class ProfileService(
    private val profileRepository: ProfileRepository,
    private val customerMapper: CustomerMapper,
) {

    fun getProfileWithAccessCheck(userId: UUID): Profile {
        val auth = SecurityContextHolder.getContext().authentication
        val loggedInEmail = auth.name

        val requester = profileRepository.findByEmail(loggedInEmail)
            ?: throw NoSuchElementException("User with email $loggedInEmail not found")

        val isEmployee = requester.role.any { it.isEmployeeRole() || it.isAdmin() }
        val isSelf = UUID.fromString(requester.id) == userId

        if (!isEmployee && !isSelf) {
            throw AccessDeniedException("You are not allowed to access this profile.")
        }

        return profileRepository.findById(userId.toString())
            .orElseThrow { NoSuchElementException("User with ID $userId not found") }
    }

    fun getAllCustomers(): List<CustomerProfileResponse> {
        val auth = SecurityContextHolder.getContext().authentication
        val loggedInEmail = auth.name

        val requester = profileRepository.findByEmail(loggedInEmail)
            ?: throw NoSuchElementException("User with email $loggedInEmail not found")

        if (!requester.role.any { it.isEmployeeRole() || it.isAdmin() }) {
            throw AccessDeniedException("You are not allowed to access this resource.")
        }

        return profileRepository.findAllByProfileType(ProfileType.CUSTOMER.name)
            .filterIsInstance<Customer>()
            .map { customerMapper.mapEntity(it) }
    }
}