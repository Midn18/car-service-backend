package com.carservice.repository

import com.carservice.model.WorkingHours
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface WorkingHoursRepository : MongoRepository<WorkingHours, String> {
    fun findByEmployeeId(employeeId: String): WorkingHours?
}