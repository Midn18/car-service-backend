package com.carservice.model.profile

data class SkinnyProfile(
    val id: String,
    val profileType: ProfileType,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val role: Set<UserRole>,
)
