package com.carservice.repository

import com.carservice.model.workingHours.EmployeeSalaryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface EmployeeSalaryRepository : JpaRepository<EmployeeSalaryEntity, String> {
    fun findByEmployeeIdAndPaymentMonth(employeeId: String, paymentMonth: LocalDate): EmployeeSalaryEntity?
    fun existsByEmployeeIdAndPaymentMonth(employeeId: String, paymentMonth: LocalDate): Boolean
}