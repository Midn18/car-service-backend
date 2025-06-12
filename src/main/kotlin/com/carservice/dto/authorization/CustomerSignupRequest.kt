package com.carservice.dto.authorization

import com.carservice.model.profile.Address
import java.time.LocalDate

data class CustomerSignupRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val phoneNumber: String,
    val dateOfBirth: LocalDate,
    val address: Address
)
