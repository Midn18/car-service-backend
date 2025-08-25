package com.carservice.model.vehicle

data class SkinnyVehicle(
    val vin: String,
    val make: String,
    val model: String,
    val year: Int,
    val engineDisplacement: Double,
    val fuelType: FuelType,
    val color: String,
    val kilometers: Int,
    val ownerId: String,
    val vehicleType: VehicleType
)
