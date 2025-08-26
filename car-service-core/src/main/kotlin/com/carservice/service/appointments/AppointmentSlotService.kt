package com.carservice.service.appointments

import com.carservice.model.appointment.AppointmentSlot
import com.carservice.model.appointment.WorkingSchedule
import com.carservice.repository.AppointmentSlotRepository
import com.carservice.repository.WorkingHoursRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Service
class AppointmentSlotService(
    private val appointmentSlotRepository: AppointmentSlotRepository,
    private val workingHoursRepository: WorkingHoursRepository
) {

    fun generateSlotsForNewWorkingHours(since: LocalDateTime) {
        val workingHoursList = workingHoursRepository.findByCreatedAtAfter(since)

        for (wh in workingHoursList) {
            for (schedule in wh.workingSchedule) {
                val workingDate = schedule.date

                if (workingDate.isBefore(LocalDate.now())) {
                    println("Skipping ${workingDate} - past date for employee ${wh.employeeId}")
                    continue
                }

                val startOfDay = workingDate.atStartOfDay()
                val endOfDay = workingDate.atTime(LocalTime.MAX)

                val slotsExist = appointmentSlotRepository.existsByEmployeeIdAndStartTimeBetween(
                    wh.employeeId,
                    startOfDay,
                    endOfDay
                )

                if (slotsExist) {
                    println("Slots already exist for ${workingDate} and employee ${wh.employeeId}, skipping...")
                    continue
                }

                val slots = generateSlotsForWorkingHours(wh.employeeId, schedule)
                if (slots.isNotEmpty()) {
                    appointmentSlotRepository.saveAll(slots)
                    println("Generated ${slots.size} slots for working hours on ${workingDate} for employee ${wh.employeeId}")
                }
            }
        }
    }

    private fun generateSlotsForWorkingHours(employeeId: String, schedule: WorkingSchedule): List<AppointmentSlot> {
        val slots = mutableListOf<AppointmentSlot>()

        val start = schedule.date.atTime(schedule.startAt)
        val end = schedule.date.atTime(schedule.endAt)

        var current = start
        while (current.plusMinutes(15) <= end) {
            slots.add(
                AppointmentSlot(
                    startTime = current,
                    endTime = current.plusMinutes(15),
                    isAvailable = true,
                    employeeId = employeeId
                )
            )
            current = current.plusMinutes(15)
        }

        return slots
    }
}

@Component
class SlotScheduler(private val slotService: AppointmentSlotService) {

    @Scheduled(cron = "\${scheduler.cron.appointment-slot-generator}")
    fun generateSlotsForRecentWorkingHours() {
        val since = LocalDateTime.now().minusHours(24)
        println("Scheduled job started: Generating appointment slots for working hours created since $since ...")
        slotService.generateSlotsForNewWorkingHours(since)
    }
}