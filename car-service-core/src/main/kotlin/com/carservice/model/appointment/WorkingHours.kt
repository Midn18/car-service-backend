package com.carservice.model.appointment

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.util.UUID

@Document("working_hours")
data class WorkingHours(
    @Id
    val id: String = UUID.randomUUID().toString(),
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    val employeeId: String?,
    val date: String,
    val startAt: String,
    val endAt: String,

    @CreatedDate
    val createdAt: LocalDateTime? = null,
    @LastModifiedDate
    val updatedAt: LocalDateTime? = null,
    @Version
    val version: Long? = null
)