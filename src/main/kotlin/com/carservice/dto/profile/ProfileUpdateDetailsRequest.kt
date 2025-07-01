package com.carservice.dto.profile

import com.carservice.model.profile.Address
import java.time.LocalDate

data class ProfileUpdateDetailsRequest(
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val password: String? = null,
    val phoneNumber: String? = null,
    val profileType: String? = null,
    val role: Set<String>? = null,
    val address: Address? = null,
    val dateOfBirth: LocalDate? = null,
)
