package com.carservice.controller

import com.carservice.service.ProfileService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/profile")
class ProfileController(private val profileService: ProfileService) {

}