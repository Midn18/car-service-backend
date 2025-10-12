package com.carservice.model.appointment

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

@Document("working_hours")
data class WorkingHours(
    @Id
    val id: String = UUID.randomUUID().toString(),
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    val employeeId: String,
    val workingSchedule: List<WorkingSchedule>,
    @CreatedDate
    val createdAt: LocalDateTime? = null,
    @LastModifiedDate
    val updatedAt: LocalDateTime? = null,
    @Version
    val version: Long? = null
)

data class WorkingSchedule(
    @field:NotNull
    val date: LocalDate,
    @field:NotNull
    val startAt: LocalTime,
    @field:NotNull
    val endAt: LocalTime
) {
    init {
        if (startAt >= endAt) {
            throw IllegalArgumentException("startAt must be before endAt")
        }
    }
}
