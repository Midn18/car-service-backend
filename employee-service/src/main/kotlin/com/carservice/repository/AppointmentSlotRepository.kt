package com.carservice.repository

import com.carservice.model.workingHours.AppointmentSlot
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface AppointmentSlotRepository : JpaRepository<AppointmentSlot, String> {
    fun existsByStartTimeBetween(startTime: LocalDateTime, endTime: LocalDateTime): Boolean
    fun existsByEmployeeIdAndStartTimeBetween(
        employeeId: String,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): Boolean

    fun findAllByEmployeeId(employeeId: String): List<AppointmentSlot>
    fun findByEmployeeIdAndStartTime(employeeId: String, startTime: LocalDateTime): AppointmentSlot?
    fun findByEmployeeIdAndStartTimeBetween(
        employeeId: String,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): List<AppointmentSlot>
}