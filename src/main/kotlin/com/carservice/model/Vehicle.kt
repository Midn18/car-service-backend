package com.carservice.model

import com.carservice.model.profile.SkinnyProfile
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
