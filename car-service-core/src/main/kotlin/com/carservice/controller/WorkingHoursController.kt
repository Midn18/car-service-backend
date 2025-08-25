package com.carservice.controller

import com.carservice.model.appointment.WorkingHours
import com.carservice.service.appointments.AppointmentSlotService
import com.carservice.service.appointments.WorkingHoursService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/working-hours")
class WorkingHoursController(
    private val workingHoursService: WorkingHoursService,
    private val appointmentSlotService: AppointmentSlotService
) {
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/employee/{employeeId}/schedule")
    fun setWorkingHours(
        @PathVariable employeeId: String,
        @RequestBody workingHours: List<WorkingHours>
    ): ResponseEntity<String> {
        val responseMessage = workingHoursService.setWorkingHours(employeeId, workingHours)
        return ResponseEntity.ok(responseMessage)
    }

    @GetMapping("/employee/{employeeId}/schedule")
    fun getWorkingHours(@PathVariable employeeId: String): ResponseEntity<WorkingHours?> {
        val workingHours = workingHoursService.getWorkingHoursByEmployeeId(employeeId)
        return if (workingHours != null) {
            ResponseEntity.ok(workingHours)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/slots")
    fun generateTimeSlots(
        @RequestParam(defaultValue = "24") hoursBack: Long
    ): ResponseEntity<String> {
        val since = LocalDateTime.now().minusHours(hoursBack)
        appointmentSlotService.generateSlotsForNewWorkingHours(since)
        return ResponseEntity.ok("Time slots successfully generated for last $hoursBack hours")
    }
}