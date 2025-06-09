package com.carservice.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Customer(
    @Id val id: String,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val phone: String = "",
    val address: String = "",
    val vehicles: List<Vehicle>,
    val serviceVisits: List<ServiceVisit>,
)
