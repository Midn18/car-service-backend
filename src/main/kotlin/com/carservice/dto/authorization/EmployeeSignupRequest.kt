package com.carservice.dto.authorization

import com.carservice.model.profile.Address
import com.carservice.model.profile.UserRole
import java.time.LocalDate

data class EmployeeSignupRequest(
    override val firstName: String,
    override val lastName: String,
    override val email: String,
    override val password: String,
    override val phoneNumber: String,
    val dateOfBirth: LocalDate,
    val role: Set<UserRole>,
    override val address: Address
) : SignUpRequest
