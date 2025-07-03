package com.carservice.mapper

import com.carservice.dto.vehicle.VehicleCreateRequest
import com.carservice.model.Vehicle
import com.carservice.model.profile.Profile
import com.carservice.model.profile.toSkinnyProfile
import org.springframework.stereotype.Component

@Component
class VehicleMapper : BiDirectionalMapper<VehicleCreateRequest, Vehicle> {

    fun mapDtoWithOwner(dto: VehicleCreateRequest, owner: Profile): Vehicle {
        return Vehicle(
            vin = dto.vin,
            make = dto.make,
            model = dto.model,
            year = dto.year,
            engineDisplacement = dto.engineDisplacement,
            fuelType = dto.fuelType,
            color = dto.color,
            kilometers = dto.kilometers,
            owner = owner.toSkinnyProfile(),
            vehicleType = dto.vehicleType,
            registrationNumber = dto.registrationNumber
        )
    }

    override fun mapDto(dto: VehicleCreateRequest): Vehicle {
        return Vehicle(
            vin = dto.vin,
            make = dto.make,
            model = dto.model,
            year = dto.year,
            engineDisplacement = dto.engineDisplacement,
            fuelType = dto.fuelType,
            color = dto.color,
            kilometers = dto.kilometers,
            owner = dto.owner,
            vehicleType = dto.vehicleType,
            registrationNumber = dto.registrationNumber
        )
    }

    override fun mapEntity(entity: Vehicle): VehicleCreateRequest {
        return VehicleCreateRequest(
            vin = entity.vin,
            make = entity.make,
            model = entity.model,
            year = entity.year,
            engineDisplacement = entity.engineDisplacement,
            fuelType = entity.fuelType,
            color = entity.color,
            kilometers = entity.kilometers,
            owner = entity.owner,
            vehicleType = entity.vehicleType,
            registrationNumber = entity.registrationNumber
        )
    }

    override val entityClass: Class<Vehicle>
        get() = Vehicle::class.java
}