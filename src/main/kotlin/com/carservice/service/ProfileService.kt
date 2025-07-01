package com.carservice.service

import com.carservice.dto.profile.CustomerProfileResponse
import com.carservice.dto.profile.EmployeeProfileResponse
import com.carservice.dto.profile.ProfileUpdateDetailsRequest
import com.carservice.exceptions.ProfileNotFoundException
import com.carservice.mapper.profile.CustomerMapper
import com.carservice.mapper.profile.EmployeeMapper
import com.carservice.model.profile.Address
import com.carservice.model.profile.Customer
import com.carservice.model.profile.Employee
import com.carservice.model.profile.Profile
import com.carservice.model.profile.UserRole
import com.carservice.model.profile.isAdmin
import com.carservice.model.profile.isEmployeeRole
import java.time.LocalDate
import com.carservice.repository.ProfileRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class ProfileService(
    private val profileRepository: ProfileRepository,
    private val customerMapper: CustomerMapper,
    private val employeeMapper: EmployeeMapper,
    private val passwordEncoder: PasswordEncoder
) {

    fun getProfileWithAccessCheck(userId: UUID): Profile {
        val requester = getRequester()

        val isSelf = requester.id == userId.toString()
        if (!hasEmployeePrivileges(requester) && !isSelf) {
            throw AccessDeniedException("You are not allowed to access this profile.")
        }

        return profileRepository.findById(userId.toString())
            .orElseThrow { NoSuchElementException("User with ID $userId not found") }
    }

    fun getAllCustomers(
        pageNumber: Int? = null,
        pageSize: Int? = null,
        firstName: String? = null,
        lastName: String? = null,
        email: String? = null,
        phoneNumber: String? = null,
        carNumber: String? = null,
        carVin: String? = null
    ): List<CustomerProfileResponse> {
        checkEmployeePrivileges()

        val pageable = if (pageNumber != null && pageSize != null) {
            PageRequest.of(pageNumber - 1, pageSize)
        } else {
            Pageable.unpaged()
        }

        val filteredCustomers = profileRepository.findAllCustomersByFilters(
            pageable, firstName, lastName, email, phoneNumber, carNumber, carVin
        )

        return filteredCustomers.content.map { customerMapper.mapEntity(it) }
    }

    fun getAllEmployees(
        pageNumber: Int? = null,
        pageSize: Int? = null,
        firstName: String? = null,
        lastName: String? = null,
        email: String? = null,
        phoneNumber: String? = null,
        role: String? = null
    ): List<EmployeeProfileResponse> {
        checkEmployeePrivileges()
        val pageable = if (pageNumber != null && pageSize != null) {
            PageRequest.of(pageNumber - 1, pageSize)
        } else {
            Pageable.unpaged()
        }
        val filteredEmployees = profileRepository.findAllEmployeesByFilters(
            pageable, firstName, lastName, email, phoneNumber, role
        )
        return filteredEmployees.content.map { employeeMapper.mapEntity(it) }
    }

    fun updateProfile(userId: UUID, request: ProfileUpdateDetailsRequest): Profile {
        val existingProfile = profileRepository.findById(userId.toString())
            .orElseThrow { NoSuchElementException("Profile with ID $userId not found") }

        val newRoles = request.role?.map { UserRole.valueOf(it) }?.toSet() ?: existingProfile.role

        when (existingProfile) {
            is Customer -> {
                if (newRoles.any { it !in setOf(UserRole.GUEST, UserRole.LOYAL, UserRole.REGULAR) }) {
                    throw IllegalArgumentException("Invalid role for Customer profile.")
                }
            }

            is Employee -> {
                if (newRoles.any { !(it.isEmployeeRole() || it.isAdmin()) }) {
                    throw IllegalArgumentException("Invalid role for Employee profile.")
                }
            }

            else -> throw IllegalArgumentException("Unsupported profile type: ${existingProfile.javaClass.simpleName}")
        }

        val updatedProfile = copyCommonDetails(existingProfile, request, newRoles)
        return profileRepository.save(updatedProfile)
    }

    fun deleteProfile(id: UUID) {
        checkEmployeePrivileges()

        profileRepository.findById(id.toString())
            .orElseThrow { ProfileNotFoundException(id.toString()) }
        return profileRepository.deleteById(id.toString())
    }

    private fun getRequester(): Profile {
        val auth = SecurityContextHolder.getContext().authentication
        val loggedInEmail = auth.name

        return profileRepository.findByEmail(loggedInEmail)
            ?: throw NoSuchElementException("User with email $loggedInEmail not found")
    }

    private fun hasEmployeePrivileges(profile: Profile): Boolean {
        return profile.role.any { it.isEmployeeRole() || it.isAdmin() }
    }

    private fun checkEmployeePrivileges() {
        val requester = getRequester()
        if (!hasEmployeePrivileges(requester)) {
            throw AccessDeniedException("You are not allowed to access this resource.")
        }
    }

    private fun copyCommonDetails(
        existing: Profile,
        request: ProfileUpdateDetailsRequest,
        newRoles: Set<UserRole>
    ): Profile = when (existing) {
        is Customer -> updateCustomerProfile(existing, request, newRoles)
        is Employee -> updateEmployeeProfile(existing, request, newRoles)
        else -> throw IllegalArgumentException("Unsupported profile type: ${existing.javaClass.simpleName}")
    }

    private fun updateCommonProfileFields(
        existing: Profile,
        request: ProfileUpdateDetailsRequest,
        newRoles: Set<UserRole>
    ): Map<String, Any> {
        return mapOf(
            "firstName" to (request.firstName ?: existing.firstName),
            "lastName" to (request.lastName ?: existing.lastName),
            "email" to (request.email ?: existing.email),
            "password" to (request.password?.let { passwordEncoder.encode(it) } ?: existing.password),
            "phoneNumber" to (request.phoneNumber ?: existing.phoneNumber),
            "address" to (request.address ?: existing.address),
            "dateOfBirth" to (request.dateOfBirth ?: existing.dateOfBirth),
            "role" to newRoles
        )
    }

    private fun updateCustomerProfile(
        customer: Customer,
        request: ProfileUpdateDetailsRequest,
        newRoles: Set<UserRole>
    ): Customer {
        val commonFields = updateCommonProfileFields(customer, request, newRoles)
        return customer.copy(
            firstName = commonFields["firstName"] as String,
            lastName = commonFields["lastName"] as String,
            email = commonFields["email"] as String,
            password = commonFields["password"] as String,
            phoneNumber = commonFields["phoneNumber"] as String,
            address = commonFields["address"] as Address,
            dateOfBirth = commonFields["dateOfBirth"] as LocalDate,
            role = commonFields["role"] as Set<UserRole>
        )
    }

    private fun updateEmployeeProfile(
        employee: Employee,
        request: ProfileUpdateDetailsRequest,
        newRoles: Set<UserRole>
    ): Employee {
        val commonFields = updateCommonProfileFields(employee, request, newRoles)
        return employee.copy(
            firstName = commonFields["firstName"] as String,
            lastName = commonFields["lastName"] as String,
            email = commonFields["email"] as String,
            password = commonFields["password"] as String,
            phoneNumber = commonFields["phoneNumber"] as String,
            address = commonFields["address"] as Address,
            dateOfBirth = commonFields["dateOfBirth"] as LocalDate,
            role = commonFields["role"] as Set<UserRole>
        )
    }
}
