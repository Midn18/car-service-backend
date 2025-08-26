package com.carservice.controller

import com.carservice.model.appointment.WorkingHours
import com.carservice.model.appointment.WorkingSchedule
import com.carservice.service.appointments.WorkingHoursService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/working-hours")
class WorkingHoursController(
    private val workingHoursService: WorkingHoursService
) {

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/employee/{employeeId}/schedule")
    fun setWorkingHours(
        @PathVariable employeeId: String,
        @RequestBody workingHours: List<WorkingSchedule>
    ): ResponseEntity<String> {
        val responseMessage = workingHoursService.setWorkingHours(employeeId, workingHours)
        return ResponseEntity.ok(responseMessage)
    }

    @PreAuthorize("@security.isEmployee(authentication)")
    @GetMapping("/employee/{employeeId}/schedule")
    fun getWorkingHours(@PathVariable employeeId: String): ResponseEntity<WorkingHours?> {
        val workingHours = workingHoursService.getWorkingHoursByEmployeeId(employeeId)
        return if (workingHours != null) {
            ResponseEntity.ok(workingHours)
        } else {
            ResponseEntity.notFound().build()
        }
    }
}