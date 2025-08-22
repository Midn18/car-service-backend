package com.carservice.service

import com.carservice.model.WorkingHours
import com.carservice.repository.WorkingHoursRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Service
class WorkingHoursService(
    private val workingHoursRepository: WorkingHoursRepository
) {
    fun setWorkingHours(employeeId: String, workingHoursRequest: List<WorkingHours>): String {
        val savedIds = mutableListOf<String>()
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE

        for (workingHours in workingHoursRequest) {
            val date = LocalDate.parse(workingHours.date, formatter)

            if (date.isBefore(LocalDate.now())) {
                throw IllegalArgumentException("Cannot set working hours for past date: ${workingHours.date}")
            }

            val existingHours = workingHoursRepository.findAll()
                .filter { it.employeeId == employeeId && it.date == workingHours.date }

            val newStart = LocalTime.parse(workingHours.startAt)
            val newEnd = LocalTime.parse(workingHours.endAt)

            var shouldSave = true

            for (existing in existingHours) {
                val existingStart = LocalTime.parse(existing.startAt)
                val existingEnd = LocalTime.parse(existing.endAt)

                if (existingStart == newStart && existingEnd == newEnd) {
                    shouldSave = false
                    break
                }

                if (newStart < existingEnd && newEnd > existingStart) {
                    throw IllegalArgumentException(
                        "Cannot add working hours ${workingHours.startAt}-${workingHours.endAt} for ${workingHours.date}: overlaps existing interval ${existing.startAt}-${existing.endAt}"
                    )
                }
            }

            if (shouldSave) {
                val saved = workingHoursRepository.save(workingHours.copy(employeeId = employeeId))
                savedIds.add(saved.id)
            }
        }

        return "Working hours set/added for employee $employeeId. Saved IDs: $savedIds"
    }

    fun getWorkingHoursByEmployeeId(employeeId: String): WorkingHours? {
        return workingHoursRepository.findByEmployeeId(employeeId)
    }
}