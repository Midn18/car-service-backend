package com.carservice.mapper.profile

import com.carservice.dto.profile.AddressDto
import com.carservice.mapper.BiDirectionalMapper
import com.carservice.model.profile.Address
import org.springframework.stereotype.Component

@Component
class AddressMapper : BiDirectionalMapper<AddressDto, Address> {
    override fun mapDto(dto: AddressDto): Address {
        return Address(
            street = dto.street,
            city = dto.city,
            postalCode = dto.postalCode,
            country = dto.country
        )
    }

    override fun mapEntity(entity: Address): AddressDto {
        return AddressDto(
            street = entity.street,
            city = entity.city,
            postalCode = entity.postalCode,
            country = entity.country
        )
    }

    override val entityClass: Class<Address>
        get() = Address::class.java
}