package com.carservice.service.appointments

import com.carservice.model.appointment.AppointmentSlot
import com.carservice.model.appointment.WorkingHours
import com.carservice.repository.AppointmentSlotRepository
import com.carservice.repository.WorkingHoursRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Service
class AppointmentSlotService(
    private val appointmentSlotRepository: AppointmentSlotRepository,
    private val workingHoursRepository: WorkingHoursRepository
) {

    fun generateSlotsForNewWorkingHours(since: LocalDateTime) {
        val workingHoursList = workingHoursRepository.findByCreatedAtAfter(since)

        for (wh in workingHoursList) {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val workingDate = LocalDate.parse(wh.date, formatter)

            if (workingDate.isBefore(LocalDate.now())) {
                println("Skipping ${wh.date} - past date")
                continue
            }

            val startOfDay = workingDate.atStartOfDay()
            val endOfDay = workingDate.atTime(LocalTime.MAX)

            val slotsExist = appointmentSlotRepository.existsByEmployeeIdAndStartTimeBetween(
                wh.employeeId!!,
                startOfDay,
                endOfDay
            )

            if (slotsExist) {
                println("Slots already exist for ${wh.date} and employee ${wh.employeeId}, skipping...")
                continue
            }

            val slots = generateSlotsForWorkingHours(wh)
            if (slots.isNotEmpty()) {
                appointmentSlotRepository.saveAll(slots)
                println("Generated ${slots.size} slots for working hours on ${wh.date} for employee ${wh.employeeId}")
            }
        }
    }

    private fun generateSlotsForWorkingHours(workingHours: WorkingHours): List<AppointmentSlot> {
        val slots = mutableListOf<AppointmentSlot>()

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val start = LocalDateTime.parse("${workingHours.date} ${workingHours.startAt}", formatter)
        val end = LocalDateTime.parse("${workingHours.date} ${workingHours.endAt}", formatter)

        var current = start
        while (current.plusMinutes(15) <= end) {
            slots.add(
                AppointmentSlot(
                    startTime = current,
                    endTime = current.plusMinutes(15),
                    isAvailable = true,
                    employeeId = workingHours.employeeId ?: "unassigned"
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