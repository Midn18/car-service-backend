package com.carservice.repository

import com.carservice.model.appointment.AppointmentSlot
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface AppointmentSlotRepository : MongoRepository<AppointmentSlot, String> {
    fun existsByStartTimeBetween(startTime: LocalDateTime, endTime: LocalDateTime): Boolean
    fun existsByEmployeeIdAndStartTimeBetween(
        employeeId: String,
        start: LocalDateTime,
        end: LocalDateTime
    ): Boolean
    fun findByEmployeeIdAndStartTimeBetween(
        employeeId: String,
        start: LocalDateTime,
        end: LocalDateTime
    ): List<AppointmentSlot>
    fun findAllByEmployeeId(employeeId: String): List<AppointmentSlot>
}