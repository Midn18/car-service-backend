package com.carservice.controller

import com.carservice.dto.authorization.CustomerSignupRequest
import com.carservice.dto.authorization.EmployeeSignupRequest
import com.carservice.dto.authorization.LoginRequest
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
    private val authService: AuthorizationService,
) {
    @PostMapping("/signup/customer")
    fun signupCustomer(@RequestBody request: CustomerSignupRequest): ResponseEntity<Profile> {
        val profile = authService.signupCustomer(request)
        return ResponseEntity.ok(profile)
    }

    @PostMapping("/signup/employee")
    fun signupEmployee(@RequestBody request: EmployeeSignupRequest): ResponseEntity<Profile> {
        val profile = authService.signupEmployee(request)
        return ResponseEntity.ok(profile)
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<Map<String, String>> {
        val token = authService.login(request)
        return ResponseEntity.ok(mapOf("token" to token))
    }
}
