package com.carservice.mapper.profile

import com.carservice.dto.profile.EmployeeProfileResponse
import com.carservice.mapper.EntityMapper
import com.carservice.model.profile.Employee
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class EmployeeMapper : EntityMapper<EmployeeProfileResponse, Employee> {

    override fun mapEntity(entity: Employee): EmployeeProfileResponse {
        return EmployeeProfileResponse(
            id = UUID.fromString(entity.id),
            profileType = entity.profileType,
            firstName = entity.firstName,
            lastName = entity.lastName,
            email = entity.email,
            phoneNumber = entity.phoneNumber,
            dateOfBirth = entity.dateOfBirth,
            address = entity.address,
            role = entity.role
        )
    }

    override val entityClass: Class<Employee>
        get() = Employee::class.java
}