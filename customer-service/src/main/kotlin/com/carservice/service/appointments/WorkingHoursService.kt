package com.carservice.service.appointments

import com.carservice.model.appointment.WorkingHours
import com.carservice.model.appointment.WorkingSchedule
import com.carservice.repository.WorkingHoursRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class WorkingHoursService(
    private val workingHoursRepository: WorkingHoursRepository
) {
    fun setWorkingHours(employeeId: String, workingHoursRequest: List<WorkingSchedule>): String {
        val today = LocalDate.now()
        workingHoursRequest.forEach { schedule ->
            if (schedule.date.isBefore(today)) {
                throw IllegalArgumentException("Cannot set working hours for past date: ${schedule.date}")
            }
        }

        workingHoursRequest.groupBy { it.date }.forEach { (date, schedules) ->
            schedules.forEachIndexed { index, newSchedule ->
                schedules.subList(index + 1, schedules.size).forEach { otherSchedule ->
                    if (newSchedule.startAt < otherSchedule.endAt && newSchedule.endAt > otherSchedule.startAt) {
                        throw IllegalArgumentException(
                            "Cannot add working hours ${newSchedule.startAt}-${newSchedule.endAt} for $date: " +
                                    "overlaps with ${otherSchedule.startAt}-${otherSchedule.endAt} in the request"
                        )
                    }
                }
            }
        }

        val existing = workingHoursRepository.findByEmployeeId(employeeId)
        val updatedSchedule = existing?.workingSchedule?.toMutableList() ?: mutableListOf()

        workingHoursRequest.forEach { newSchedule ->
            updatedSchedule.filter { it.date == newSchedule.date }.forEach { existingSchedule ->
                if (newSchedule.startAt < existingSchedule.endAt && newSchedule.endAt > existingSchedule.startAt) {
                    throw IllegalArgumentException(
                        "Cannot add working hours ${newSchedule.startAt}-${newSchedule.endAt} for ${newSchedule.date}: " +
                                "overlaps existing interval ${existingSchedule.startAt}-${existingSchedule.endAt}"
                    )
                }
            }
        }

        workingHoursRequest.forEach { newSchedule -> updatedSchedule.add(newSchedule) }

        val workingHours = WorkingHours(
            employeeId = employeeId,
            workingSchedule = updatedSchedule
        )
        workingHoursRepository.save(workingHours)

        return "Working hours set/added for employee $employeeId. Updated schedule with ${updatedSchedule.size} entries"
    }

    fun getWorkingHoursByEmployeeId(employeeId: String): WorkingHours? {
        return workingHoursRepository.findByEmployeeId(employeeId)
            ?: throw NoSuchElementException("Working hours not found for employeeId: $employeeId")
    }
}