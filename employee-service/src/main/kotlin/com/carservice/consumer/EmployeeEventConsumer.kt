package com.carservice.consumer

import com.carservice.event.EmployeeCreatedEvent
import com.carservice.model.workingHours.EmployeeSalaryEntity
import com.carservice.repository.EmployeeSalaryRepository
import org.hibernate.StaleObjectStateException
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDate

@Component
class EmployeeEventConsumer(
    private val salaryRepository: EmployeeSalaryRepository
) {

    private val logger = LoggerFactory.getLogger(EmployeeEventConsumer::class.java)

    @KafkaListener(
        topics = ["employee.created"],
        groupId = "employee-service-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun onEmployeeCreated(event: EmployeeCreatedEvent) {
        val paymentMonth = LocalDate.now().withDayOfMonth(1)

        val salary = EmployeeSalaryEntity(
            employeeId = event.employeeId,
            paymentMonth = paymentMonth,
            baseSalary = BigDecimal.ZERO,
            bonus = BigDecimal.ZERO
        )

        try {
            salaryRepository.save(salary)
            logger.info("Salary initialized for employee ${event.employeeId}")
        } catch (ex: Exception) {

            if (ex is DataIntegrityViolationException) {
                logger.error("Salary already exists for ${event.employeeId}, ignoring duplicate.")
                return
            }

            if (ex is ObjectOptimisticLockingFailureException || ex.cause is StaleObjectStateException
            ) {
                println("Race condition on salary creation for ${event.employeeId}, ignoring.")
                return
            }

            logger.error("Failed to initialize salary for employee ${event.employeeId}: ${ex.message}", ex)
            throw ex
        }
    }
}