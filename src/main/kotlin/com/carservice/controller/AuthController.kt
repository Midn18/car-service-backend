package com.carservice.controller

import com.carservice.api.ApiApi
import com.carservice.dto.authorization.CustomerSignupRequest
import com.carservice.dto.authorization.EmployeeSignupRequest
import com.carservice.service.auth.AuthorizationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(private val authService: AuthorizationService) : ApiApi {
    fun apiAuthSignupCustomerPost(customerSignupRequest: CustomerSignupRequest): ResponseEntity<Unit> {
        authService.signupCustomer(customerSignupRequest)
        return ResponseEntity.status(201).build()
    }

    fun apiAuthSignupEmployeePost(employeeSignupRequest: EmployeeSignupRequest): ResponseEntity<Unit> {
        authService.signupEmployee(employeeSignupRequest)
        return ResponseEntity.status(201).build()
    }
}