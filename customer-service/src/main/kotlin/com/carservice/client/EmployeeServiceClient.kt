package com.carservice.client

import com.carservice.event.EmployeeCreatedEvent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class EmployeeServiceClient(
    private val restClient: RestClient,
    @Value("\${services.employee.url}") private val employeeServiceUrl: String
) {
    private val logger = LoggerFactory.getLogger(EmployeeServiceClient::class.java)

    fun initializeSalary(event: EmployeeCreatedEvent) {
        try {
            restClient.post()
                .uri("$employeeServiceUrl/internal/employees/salary/init")
                .body(event)
                .retrieve()
                .toBodilessEntity()
            logger.info("Salary initialization requested for employee ${event.employeeId}")
        } catch (ex: Exception) {
            logger.error("Failed to initialize salary for employee ${event.employeeId}: ${ex.message}", ex)
            throw ex
        }
    }
}

