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

    fun generateAllSlots() {
        val workingHoursList = workingHoursRepository.findAll()

        for (wh in workingHoursList) {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val workingDate = LocalDate.parse(wh.date, formatter)

            if (workingDate.isBefore(LocalDate.now())) {
                println("Skipping ${wh.date} - past date")
                continue
            }

            val startOfDay = workingDate.atStartOfDay()
            val endOfDay = workingDate.atTime(LocalTime.MAX)

            if (appointmentSlotRepository.existsByStartTimeBetween(startOfDay, endOfDay)) {
                println("Slots already exist for ${wh.date}, skipping...")
                continue
            }

            val slots = generateSlotsForWorkingHours(wh)
            if (slots.isNotEmpty()) {
                appointmentSlotRepository.saveAll(slots)
                println("Generated ${slots.size} slots for working hours on ${wh.date}")
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
                    isAvailable = true
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
    fun generateSlotsForAllWorkingHours() {
        println("Scheduled job started: Generating appointment slots...")
        slotService.generateAllSlots()
    }
}