package com.carservice.repository

import com.carservice.model.appointment.WorkingHours
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface WorkingHoursRepository : MongoRepository<WorkingHours, String> {
    fun findByEmployeeId(employeeId: String): WorkingHours?
    fun findByCreatedAtAfter(createdAt: LocalDateTime): List<WorkingHours>
}