package com.carservice.controller

import com.carservice.dto.profile.CustomerProfileResponse
import com.carservice.mapper.profile.ProfileMapper
import com.carservice.service.ProfileService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/profile")
class ProfileController(
    private val profileService: ProfileService,
    private val profileMapper: ProfileMapper
) {

    @GetMapping("/{id}")
    fun getProfileById(@PathVariable id: UUID): ResponseEntity<Any> {
        val domainProfile = profileService.getProfileWithAccessCheck(id)
        val apiProfile = profileMapper.toApiProfile(domainProfile)
        return ResponseEntity.ok(apiProfile)
    }

    @GetMapping("/customers")
    fun getAllCustomers(): ResponseEntity<List<CustomerProfileResponse>> {
        val customers = profileService.getAllCustomers()
        return ResponseEntity.ok(customers)
    }
}