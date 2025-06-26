package com.carservice.dto.authorization

import com.carservice.model.profile.Address
import java.time.LocalDate

interface SignUpRequest {
    val firstName: String
    val lastName: String
    val email: String
    val password: String
    val phoneNumber: String
    val dateOfBirth: LocalDate
    val address: Address
}