package com.carservice.dto.vehicle

import com.carservice.model.vehicle.FuelType

data class VehicleUpdateRequest(
    val color: String?,
    val kilometers: Int?,
    val registrationNumber: String?,
    val make: String?,
    val model: String?,
    val year: Int?,
    val engineDisplacement: Double?,
    val fuelType: FuelType?
)