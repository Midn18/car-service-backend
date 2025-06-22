package com.carservice.controller

import com.carservice.api.AuthApi
import com.carservice.model.Customer
import com.carservice.model.CustomerSignupRequest
import com.carservice.model.Employee
import com.carservice.model.EmployeeSignupRequest
import com.carservice.service.auth.AuthorizationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(private val authService: AuthorizationService) : AuthApi {

    override fun authSignupEmployeePost(
        @RequestBody employeeSignupRequest: EmployeeSignupRequest
    ): ResponseEntity<Employee> {
        authService.signupEmployee(employeeSignupRequest)
        return ResponseEntity.status(201).build()
    }

    override fun authSignupCustomerPost(
        @RequestBody customerSignupRequest: CustomerSignupRequest
    ): ResponseEntity<Customer> {
        authService.signupCustomer(customerSignupRequest)
        return ResponseEntity.status(201).build()
    }
}