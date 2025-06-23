package com.carservice.controller

import com.carservice.api.ProfileApi
import com.carservice.mapper.ProfileMapper
import com.carservice.service.ProfileService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class ProfileController(
    private val profileService: ProfileService,
    private val profileMapper: ProfileMapper
) : ProfileApi {

    override fun getProfileById(id: UUID): ResponseEntity<Any> {
        val domainProfile = profileService.getProfileWithAccessCheck(id)
        val apiProfile = profileMapper.toApiProfile(domainProfile)
        return ResponseEntity.ok(apiProfile)
    }
}