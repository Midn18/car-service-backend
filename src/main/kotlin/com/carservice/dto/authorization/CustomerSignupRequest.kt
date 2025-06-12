package com.carservice.dto.authorization

import com.carservice.model.profile.Address
import java.time.LocalDate

data class CustomerSignupRequest(
    override val firstName: String,
    override val lastName: String,
    override val email: String,
    override val password: String,
    override val phoneNumber: String,
    val dateOfBirth: LocalDate,
    override val address: Address
) : SignUpRequest