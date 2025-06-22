package com.carservice.controller

import com.carservice.api.ProfileApi
import com.carservice.mapper.ProfileMapper
import com.carservice.model.Profile
import com.carservice.service.ProfileService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class ProfileController(
    private val profileService: ProfileService,
    private val profileMapper: ProfileMapper
) : ProfileApi {

//    TODO: review permissions and access control
    override fun getProfileById(id: UUID): ResponseEntity<Profile> {
        val domainProfile = profileService.getProfileWithAccessCheck(id)
        val apiProfile = profileMapper.toApiProfile(domainProfile)
        return ResponseEntity.ok(apiProfile)
    }
}