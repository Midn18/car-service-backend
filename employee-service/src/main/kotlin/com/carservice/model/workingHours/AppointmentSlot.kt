package com.carservice.model.workingHours

import jakarta.persistence.*
import jakarta.validation.constraints.Future
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.hibernate.annotations.Where
import org.jetbrains.annotations.NotNull
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(
    name = "appointment_slots",
    indexes = [
        Index(name = "idx_employee_start", columnList = "employee_id, start_time"),
        Index(name = "idx_start_time", columnList = "start_time"),
        Index(name = "idx_employee_availability", columnList = "employee_id, is_available")
    ],
    uniqueConstraints = [
        UniqueConstraint(
            name = "uq_employee_time_range",
            columnNames = ["employee_id", "start_time", "end_time"]
        )
    ]
)
@Where(clause = "is_deleted = false")
data class AppointmentSlot(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    val id: UUID = UUID.randomUUID(),

    @NotNull
    @Column(name = "employee_id", nullable = false, updatable = false)
    val employeeId: String,

    @Future(message = "startTime must be in the future")
    @Column(name = "start_time", nullable = false)
    val startTime: LocalDateTime,

    @NotNull
    @Future(message = "endTime must be in the future")
    @Column(name = "end_time", nullable = false)
    val endTime: LocalDateTime,

    @Column(name = "is_available", nullable = false)
    var isAvailable: Boolean = true,

    @Column(name = "is_deleted", nullable = false)
    var isDeleted: Boolean = false,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(name = "updated_at")
    val updatedAt: LocalDateTime? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "working_hours_id", nullable = true)
    val workingHours: WorkingHoursEntity? = null

) {
    constructor() : this(
        employeeId = "",
        startTime = LocalDateTime.now().plusHours(1),
        endTime = LocalDateTime.now().plusHours(2)
    )

    init {
        require(startTime.isBefore(endTime)) { "startTime must be before endTime" }
        require(endTime.isAfter(startTime)) { "endTime must be after startTime" }
    }

    fun book(): AppointmentSlot {
        if (!isAvailable) throw IllegalStateException("Slot is already booked")
        if (isDeleted) throw IllegalStateException("Cannot book a deleted slot")
        isAvailable = false
        return this
    }

    fun release(): AppointmentSlot {
        isAvailable = true
        return this
    }

    fun softDelete(): AppointmentSlot {
        if (isDeleted) return this
        isDeleted = true
        isAvailable = false
        return this
    }

    fun restore(): AppointmentSlot {
        isDeleted = false
        return this
    }

    fun isActiveAndAvailable(): Boolean = !isDeleted && isAvailable
}