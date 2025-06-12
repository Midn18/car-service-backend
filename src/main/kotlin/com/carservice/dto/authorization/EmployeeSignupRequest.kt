package com.carservice.dto.authorization

import com.carservice.model.profile.Address
import com.carservice.model.profile.UserRole
import java.time.LocalDate

data class EmployeeSignupRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val phoneNumber: String,
    val dateOfBirth: LocalDate,
    val role: Set<UserRole>,
    val address: Address
)
