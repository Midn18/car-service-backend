package com.carservice.model.vehicle

import com.carservice.model.ServiceVisit
import com.carservice.model.profile.SkinnyProfile
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Vehicle(
    @Id
    val vin: String,
    val make: String = "",
    val model: String = "",
    val year: Int = 0,
    val engineDisplacement: Double,
    val fuelType: FuelType,
    val color: String = "",
    val kilometers: Int,
    val owner: SkinnyProfile,
    val vehicleType: VehicleType,
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    val serviceHistory: List<ServiceVisit> = emptyList(),
    val registrationNumber: String = "",
)

enum class VehicleType {
    CAR,
    TRUCK,
    MOTORCYCLE,
    BUS,
    VAN;
}

enum class FuelType {
    GASOLINE,
    DIESEL,
    ELECTRIC,
    HYBRID,
    LPG;
}

fun Vehicle.toSkinnyVehicle(): SkinnyVehicle {
    return SkinnyVehicle(
        vin = vin,
        make = make,
        model = model,
        year = year,
        engineDisplacement = engineDisplacement,
        fuelType = fuelType,
        color = color,
        kilometers = kilometers,
        ownerId = owner.id,
        vehicleType = vehicleType
    )
}
