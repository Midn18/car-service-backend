package com.carservice.model.appointment

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Document("service_visits")
data class ServiceVisit(
    @Id @Field("id")
    val visitId: String = UUID.randomUUID().toString(),
    val vehicleId: String,
    val employeeId: String,
    val customerId: String,
    val appointmentDate: LocalDate,
    val appointmentStartAt: String,
    val appointmentEndAt: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val status: Status,
    val serviceTypes: List<ServiceType>,
    val workingPrice: Double = 0.0,
    val partsPrice: Double = 0.0,
    val totalPrice: Double = 0.0,
    val additionalDetails: String = ""
)

// To think about adding a collection for service types
//  mechanical part
//  detailing part
enum class ServiceType(val durationMinutes: Int, val basePrice: Double) {
    OIL_CHANGE(30, 50.0),
    TIRE_ROTATION(45, 60.0),
    BRAKE_SERVICE(60, 100.0),
    ENGINE_TUNE_UP(90, 150.0),
    TRANSMISSION_SERVICE(120, 200.0),
    AIR_CONDITIONING_SERVICE(60, 80.0),
    WHEEL_ALIGNMENT(45, 70.0),
    COOLANT_FLUSH(30, 40.0),
    BATTERY_REPLACEMENT(30, 50.0),
    EXHAUST_SYSTEM_REPAIR(90, 120.0),
    SUSPENSION_REPAIR(120, 180.0),
    BODYWORK_REPAIR(180, 300.0),
    ELECTRICAL_DIAGNOSTICS(60, 90.0),
    DETAILING(120, 150.0)
}

enum class Status {
    SCHEDULED,
    IN_PROGRESS,
    WAITING_FOR_PAYMENT,
    PAID,
    COMPLETED,
    CANCELLED,
}