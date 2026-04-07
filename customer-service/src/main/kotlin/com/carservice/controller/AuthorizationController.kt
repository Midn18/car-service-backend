package com.carservice.controller

import com.carservice.config.LoggerUtil
import com.carservice.dto.LoginRequest
import com.carservice.dto.LoginResponse
import com.carservice.model.profile.Customer
import com.carservice.model.profile.Employee
import com.carservice.model.profile.Profile
import com.carservice.service.auth.AuthorizationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class SignupResponse(
    val profile: Profile,
    val accessToken: String? = null
)

@RestController
@RequestMapping("/api/auth")
class AuthorizationController(
    private val authorizationService: AuthorizationService
) {
    private val logger = LoggerUtil.getLogger(AuthorizationController::class.java)

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<Any> {
        return try {
            val tokenResponse = authorizationService.login(loginRequest.email, loginRequest.password)
            ResponseEntity.ok(tokenResponse)
        } catch (e: Exception) {
            val cause = e.cause?.message ?: e.message ?: "Authentication failed"
            logger.warn("Login failed for ${loginRequest.email}: $cause")
            ResponseEntity.status(401).body(mapOf("error" to cause))
        }
    }

    @PostMapping("/signup/customer")
    fun signupCustomer(@RequestBody customer: Customer): ResponseEntity<SignupResponse> {
        val profile = authorizationService.signup(customer)
        val token = runCatching {
            authorizationService.login(profile.email, customer.password)
        }.getOrNull()
        return ResponseEntity.ok(SignupResponse(profile, token?.accessToken))
    }

    @PostMapping("/signup/employee")
    fun signupEmployee(@RequestBody employee: Employee): ResponseEntity<SignupResponse> {
        val profile = authorizationService.signup(employee)
        val token = runCatching {
            authorizationService.login(profile.email, employee.password)
        }.getOrNull()
        return ResponseEntity.ok(SignupResponse(profile, token?.accessToken))
    }
}