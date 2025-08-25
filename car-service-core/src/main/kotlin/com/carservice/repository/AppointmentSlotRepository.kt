package com.carservice.repository

import com.carservice.model.appointment.AppointmentSlot
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface AppointmentSlotRepository : MongoRepository<AppointmentSlot, String> {
    fun existsByStartTimeBetween(startTime: LocalDateTime, endTime: LocalDateTime): Boolean
}