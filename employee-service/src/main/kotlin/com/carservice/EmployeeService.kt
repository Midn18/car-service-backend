package com.carservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EmployeeService

fun main(args: Array<String>) {
    runApplication<EmployeeService>(*args)
}