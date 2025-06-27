package com.carservice.dto.profile

import com.carservice.model.profile.Address
import com.carservice.model.profile.ProfileType
import com.carservice.model.profile.UserRole
import java.time.LocalDate
import java.util.UUID

interface ProfileResponse {
    val id: UUID
    val firstName: String
    val lastName: String
    val email: String
    val phoneNumber: String
    val dateOfBirth: LocalDate
    val address: Address
    val profileType: ProfileType
    val role: Set<UserRole>
}