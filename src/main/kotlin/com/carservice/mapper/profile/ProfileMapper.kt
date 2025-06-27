package com.carservice.mapper.profile

import com.carservice.dto.profile.*
import com.carservice.model.profile.*
import org.springframework.stereotype.Component

@Component
class ProfileMapper(
    private val employeeMapper: EmployeeMapper,
    private val customerMapper: CustomerMapper
) {

    fun toApiProfile(profile: Profile): ProfileResponse {
        return when (profile) {
            is Customer -> customerMapper.mapEntity(profile)
            is Employee -> employeeMapper.mapEntity(profile)
            else -> throw IllegalArgumentException("Unknown profile type: ${profile.javaClass}")
        }
    }
}