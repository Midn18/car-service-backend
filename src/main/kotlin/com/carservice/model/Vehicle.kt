package com.carservice.model

import com.carservice.model.enum.VehicleTypeEnum
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Vehicle(
    @Id val vin: String,
    val make: String = "",
    val model: String = "",
    val year: Int = 0,
    val color: String = "",
    val kilometers: Int,
    val ownerId: String = "",
    val vehicleType: VehicleTypeEnum,
    val serviceHistory: List<ServiceVisit>,
    val registrationNumber: String = "",
    val insuranceDetails: String = ""
)
