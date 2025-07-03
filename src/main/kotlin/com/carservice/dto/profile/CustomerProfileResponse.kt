package com.carservice.dto.profile

import com.carservice.model.profile.Address
import com.carservice.model.profile.ProfileType
import com.carservice.model.profile.UserRole
import java.time.LocalDate
import java.util.UUID

data class CustomerProfileResponse(
    override val id: UUID,
    override val firstName: String,
    override val lastName: String,
    override val email: String,
    override val phoneNumber: String,
    override val dateOfBirth: LocalDate,
    override val address: Address,
    override val profileType: ProfileType,
    override val role: Set<UserRole>,
    val vehiclesVin: List<String>,
    val serviceVisitIds: List<String>,
    val visitCounter: Int
) : ProfileResponse