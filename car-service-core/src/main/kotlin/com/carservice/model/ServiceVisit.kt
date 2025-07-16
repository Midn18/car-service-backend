package com.carservice.model

import com.carservice.model.profile.SkinnyProfile
import com.carservice.model.vehicle.SkinnyVehicle
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDate
import java.util.UUID

@Document
data class ServiceVisit(
    @Id @Field("id")
    val visitId: String = UUID.randomUUID().toString(),
    val vehicleDetails: SkinnyVehicle,
    val appointmentDate: LocalDate,
    val appointmentDuration: Long,
    val status: Status,
    val serviceType: ServiceType,
    val employee: SkinnyProfile,
    val customer: SkinnyProfile,
    val price: Double = 0.0,
    val additionalDetails: String = "",
)

enum class ServiceType {
    OIL_CHANGE,
    TIRE_ROTATION,
    BRAKE_SERVICE,
    ENGINE_TUNE_UP,
    TRANSMISSION_SERVICE,
    AIR_CONDITIONING_SERVICE,
    WHEEL_ALIGNMENT,
    COOLANT_FLUSH,
    BATTERY_REPLACEMENT,
    EXHAUST_SYSTEM_REPAIR,
    SUSPENSION_REPAIR,
    BODYWORK_REPAIR,
    ELECTRICAL_DIAGNOSTICS,
    DETAILING;
}

enum class Status {
    SCHEDULED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED,
}