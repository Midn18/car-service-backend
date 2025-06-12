package com.carservice.model.profile

import java.util.UUID

data class SkinnyProfile(
    val id: UUID,
    val profileType: ProfileType,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val role: Set<UserRole>,
)
