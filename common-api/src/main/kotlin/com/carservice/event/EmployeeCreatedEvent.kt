package com.carservice.event;

import java.time.LocalDate

data class EmployeeCreatedEvent(
    val employeeId: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val roles: Set<String>,
    val hireDate: LocalDate = LocalDate.now()
)
