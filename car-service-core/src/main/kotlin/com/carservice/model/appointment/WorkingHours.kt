package com.carservice.model.appointment

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.UUID

@Document("working_hours")
data class WorkingHours(
    @Id
    val id: String = UUID.randomUUID().toString(),
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    val employeeId: String?,
    val date: String,
    val startAt: String,
    val endAt: String
)