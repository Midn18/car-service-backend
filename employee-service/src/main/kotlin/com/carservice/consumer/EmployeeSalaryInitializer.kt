package com.carservice.consumer

import com.carservice.event.EmployeeCreatedEvent
import com.carservice.model.workingHours.EmployeeSalaryEntity
import com.carservice.repository.EmployeeSalaryRepository
import org.hibernate.StaleObjectStateException
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate

@Service
class EmployeeSalaryInitializer(
    private val salaryRepository: EmployeeSalaryRepository
) {
    private val logger = LoggerFactory.getLogger(EmployeeSalaryInitializer::class.java)

    fun initializeSalary(event: EmployeeCreatedEvent) {
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
                logger.warn("Salary already exists for ${event.employeeId}, ignoring duplicate.")
                return
            }
            if (ex is ObjectOptimisticLockingFailureException || ex.cause is StaleObjectStateException) {
                logger.warn("Race condition on salary creation for ${event.employeeId}, ignoring.")
                return
            }
            logger.error("Failed to initialize salary for employee ${event.employeeId}: ${ex.message}", ex)
            throw ex
        }
    }
}

