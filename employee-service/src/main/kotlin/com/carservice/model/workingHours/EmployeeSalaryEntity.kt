package com.carservice.model.workingHours

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import jakarta.persistence.Version
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(
    name = "employee_salaries",
    indexes = [Index(name = "idx_employee_month", columnList = "employee_id, payment_month")],
    uniqueConstraints = [UniqueConstraint(name = "uq_employee_month", columnNames = ["employee_id", "payment_month"])]
)
data class EmployeeSalaryEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(name = "employee_id", nullable = false, updatable = false)
    val employeeId: String,

    @Column(name = "base_salary", nullable = false, precision = 12, scale = 2)
    var baseSalary: BigDecimal = BigDecimal.ZERO,

    @Column(name = "bonus", precision = 12, scale = 2)
    var bonus: BigDecimal = BigDecimal.ZERO,

    @Column(name = "payment_month", nullable = false)
    val paymentMonth: LocalDate,

    @Column(name = "set_by_admin_id")
    var setByAdminId: String? = null,

    @Column(name = "set_at")
    var setAt: LocalDateTime? = null,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(name = "updated_at")
    val updatedAt: LocalDateTime? = null,

    @Version
    @Column(name = "version")
    var version: Long? = null
) {
    constructor() : this(
        employeeId = "",
        paymentMonth = LocalDate.now().withDayOfMonth(1)
    )

    init {
        require(paymentMonth.dayOfMonth == 1) { "paymentMonth must be first day of month" }
    }

    fun updateSalary(
        newBaseSalary: BigDecimal,
        newBonus: BigDecimal,
        adminId: String
    ): EmployeeSalaryEntity {
        require(newBaseSalary >= BigDecimal.ZERO) { "Salary cannot be negative" }
        this.baseSalary = newBaseSalary
        this.bonus = newBonus
        this.setByAdminId = adminId
        this.setAt = LocalDateTime.now()
        return this
    }
}