package com.carservice.repository

import com.carservice.model.workingHours.WorkingHoursEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface WorkingHoursRepository : JpaRepository<WorkingHoursEntity, String> {
    fun findByEmployeeId(employeeId: String): WorkingHoursEntity?
    fun findByCreatedAtAfter(createdAt: LocalDateTime): List<WorkingHoursEntity>
}