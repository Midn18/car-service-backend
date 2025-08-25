package com.carservice.model.appointment

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.util.UUID

@Document("appointment_slots")
data class AppointmentSlot(
    @Id
    val id: String = UUID.randomUUID().toString(),
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val isAvailable: Boolean = true
)