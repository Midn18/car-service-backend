package com.carservice.controller

import com.carservice.dto.profile.ProfileUpdateDetailsRequest
import com.carservice.model.profile.Customer
import com.carservice.model.profile.Employee
import com.carservice.service.ProfileService
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/profile")
class ProfileController(
    private val profileService: ProfileService,
) {

    @GetMapping("/{id}")
    fun getProfileById(@PathVariable id: UUID): ResponseEntity<Any> {
        val profile = profileService.getProfileById(id)
        return ResponseEntity.ok(profile)
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/customers")
    fun getCustomers(
        @RequestParam(name = "page_number", required = false)
        @Min(1, message = "Page number must be greater than 0")
        pageNumber: Int?,
        @RequestParam(name = "page_size", required = false)
        @Min(1, message = "Page size must be at least 1")
        @Max(100, message = "Page size cannot be more than 100")
        pageSize: Int?,
        @RequestParam(name = "first_name", required = false)
        @Size(min = 2, max = 30, message = "First name must be between 2 and 30 characters")
        firstName: String?,
        @RequestParam(name = "last_name", required = false)
        @Size(min = 2, max = 30, message = "Last name must be between 2 and 30 characters")
        lastName: String?,
        @RequestParam(name = "email", required = false)
        email: String?,
        @RequestParam(name = "phone_number", required = false)
        phoneNumber: String?,
        @RequestParam(name = "car_vin", required = false)
        @Size(min = 17, message = "VIN must be at least 17 characters")
        carVin: String?
    ): ResponseEntity<List<Customer>> {
        val customers = profileService.getAllCustomers(
            pageNumber = pageNumber,
            pageSize = pageSize,
            firstName = firstName,
            lastName = lastName,
            email = email,
            phoneNumber = phoneNumber,
            carVin = carVin
        )
        return ResponseEntity.ok(customers)
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/employees")
    fun getEmployees(
        @RequestParam(name = "page_number", required = false)
        @Min(1, message = "Page number must be greater than 0")
        pageNumber: Int?,
        @RequestParam(name = "page_size", required = false)
        @Min(1, message = "Page size must be at least 1")
        @Max(100, message = "Page size cannot be more than 100")
        pageSize: Int?,
        @RequestParam(name = "first_name", required = false)
        @Size(min = 2, max = 30, message = "First name must be between 2 and 30 characters")
        firstName: String?,
        @RequestParam(name = "last_name", required = false)
        @Size(min = 2, max = 30, message = "Last name must be between 2 and 30 characters")
        lastName: String?,
        @RequestParam(name = "email", required = false)
        email: String?,
        @RequestParam(name = "phone_number", required = false)
        phoneNumber: String?,
        @RequestParam(name = "role", required = false)
        @Size(min = 2, max = 20, message = "Role must be between 2 and 20 characters")
        role: String?
    ): ResponseEntity<List<Employee>> {
        val employees = profileService.getAllEmployees(
            pageNumber = pageNumber,
            pageSize = pageSize,
            firstName = firstName,
            lastName = lastName,
            email = email,
            phoneNumber = phoneNumber,
            role = role
        )
        return ResponseEntity.ok(employees)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    fun deleteProfile(@PathVariable id: UUID): ResponseEntity<String> {
        profileService.deleteProfile(id)
        return ResponseEntity.ok("Profile with ID $id has been deleted successfully.")
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    fun updateProfile(
        @PathVariable id: UUID,
        @RequestBody profileUpdateRequest: ProfileUpdateDetailsRequest
    ): ResponseEntity<Any> {
        val updatedProfile = profileService.updateProfile(id, profileUpdateRequest)
        return ResponseEntity.ok(updatedProfile)
    }
}