package com.carservice.model

import com.carservice.model.enum.EmployeeTypeEnum
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Employee(
    @Id val id: String,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val phone: String = "",
    val role: EmployeeTypeEnum,
    val address: String = "",
)