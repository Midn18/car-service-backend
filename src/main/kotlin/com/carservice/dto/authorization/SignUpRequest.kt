package com.carservice.dto.authorization

import com.carservice.model.profile.Address

interface SignUpRequest {
    val firstName: String
    val lastName: String
    val email: String
    val password: String
    val phoneNumber: String
    val address: Address
}