package com.carservice.dto.vehicle

import com.carservice.model.FuelType
import com.carservice.model.VehicleType
import com.carservice.model.profile.SkinnyProfile

data class VehicleCreateRequest(
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
    val registrationNumber: String = ""
)