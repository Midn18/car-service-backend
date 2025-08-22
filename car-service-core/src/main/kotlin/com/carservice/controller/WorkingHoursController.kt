package com.carservice.controller

import com.carservice.model.WorkingHours
import com.carservice.service.WorkingHoursService
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
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/employee/{employeeId}/schedule")
    fun setWorkingHours(
        @PathVariable employeeId: String,
        @RequestBody workingHours: List<WorkingHours>
    ): String {
        return workingHoursService.setWorkingHours(employeeId, workingHours)
    }

    @GetMapping("/employee/{employeeId}/schedule")
    fun getWorkingHours(@PathVariable employeeId: String): WorkingHours? {
        return workingHoursService.getWorkingHoursByEmployeeId(employeeId)
    }
}