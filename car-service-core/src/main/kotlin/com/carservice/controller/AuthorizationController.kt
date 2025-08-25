package com.carservice.controller

import com.carservice.model.profile.Customer
import com.carservice.model.profile.Employee
import com.carservice.model.profile.Profile
import com.carservice.service.auth.AuthorizationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthorizationController(
    private val authService: AuthorizationService
) {
    @PostMapping("/signup/customer")
    fun signupCustomer(@RequestBody request: Customer): ResponseEntity<Profile> {
        val profile = authService.signup(request)
        return ResponseEntity.ok(profile)
    }

    @PostMapping("/signup/employee")
    fun signupEmployee(@RequestBody request: Employee): ResponseEntity<Profile> {
        val profile = authService.signup(request)
        return ResponseEntity.ok(profile)
    }
}