package com.carservice.controller

import com.carservice.service.appointments.AppointmentSlotService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/slots")
class SlotsController(
    private val appointmentSlotService: AppointmentSlotService
) {

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/slots")
    fun generateTimeSlots(
        @RequestParam(defaultValue = "24") hoursBack: Long
    ): ResponseEntity<String> {
        val since = LocalDateTime.now().minusHours(hoursBack)
        appointmentSlotService.generateSlotsForNewWorkingHours(since)
        return ResponseEntity.ok("Time slots successfully generated for last $hoursBack hours")
    }
}