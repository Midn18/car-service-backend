package com.carservice.mapper.profile

import com.carservice.dto.profile.CustomerProfileResponse
import com.carservice.mapper.EntityMapper
import com.carservice.model.profile.Customer
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class CustomerMapper : EntityMapper<CustomerProfileResponse, Customer> {

    override fun mapEntity(entity: Customer): CustomerProfileResponse {
        return CustomerProfileResponse(
            id = UUID.fromString(entity.id),
            profileType = entity.profileType,
            firstName = entity.firstName,
            lastName = entity.lastName,
            email = entity.email,
            phoneNumber = entity.phoneNumber,
            dateOfBirth = entity.dateOfBirth,
            address = entity.address,
            role = entity.role,
            vehiclesVin = entity.vehiclesVin,
            serviceVisitIds = entity.serviceVisitIds,
            visitCounter = entity.visitCounter
        )
    }

    override val entityClass: Class<Customer>
        get() = Customer::class.java
}