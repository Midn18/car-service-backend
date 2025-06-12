package com.carservice.service

import com.carservice.repository.ProfileRepository
import org.springframework.stereotype.Service

@Service
class ProfileService(
    val profileRepository: ProfileRepository
) {
}