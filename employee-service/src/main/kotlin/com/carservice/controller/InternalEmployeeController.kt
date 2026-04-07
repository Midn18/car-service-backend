package com.carservice.controller

import com.carservice.consumer.EmployeeSalaryInitializer
import com.carservice.event.EmployeeCreatedEvent
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/internal/employees")
class InternalEmployeeController(
    private val salaryInitializer: EmployeeSalaryInitializer
) {

    @PostMapping("/salary/init")
    fun initSalary(@RequestBody event: EmployeeCreatedEvent): ResponseEntity<Void> {
        salaryInitializer.initializeSalary(event)
        return ResponseEntity.ok().build()
    }
}

