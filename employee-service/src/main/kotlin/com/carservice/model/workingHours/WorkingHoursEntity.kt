package com.carservice.model.workingHours

import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Embeddable
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Table
import jakarta.persistence.Version
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.jetbrains.annotations.NotNull
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

@Entity
@Table(name = "working_hours")
data class WorkingHoursEntity(
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "employee_id", nullable = false)
    val employeeId: String,

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "working_schedule", joinColumns = [JoinColumn(name = "working_hours_id")])
    val schedule: List<WorkingSchedule> = emptyList(),

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(name = "updated_at")
    val updatedAt: LocalDateTime? = null,

    @Version
    val version: Long? = null
) {
    constructor() : this(employeeId = "")
}

@Embeddable
data class WorkingSchedule(
    @NotNull @Column(name = "date") val date: LocalDate,
    @NotNull @Column(name = "start_at") val startAt: LocalTime,
    @NotNull @Column(name = "end_at") val endAt: LocalTime
) {
    init {
        require(startAt < endAt) { "startAt must be before endAt" }
    }

    constructor() : this(LocalDate.now(), LocalTime.MIN, LocalTime.MIN)
}