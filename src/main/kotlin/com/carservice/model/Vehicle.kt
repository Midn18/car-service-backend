package com.carservice.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.UUID

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
    val ownerId: UUID,
    val vehicleType: VehicleType,
    val serviceHistory: List<ServiceVisit>,
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
