package com.carservice.controller

import com.carservice.model.profile.Customer
import com.carservice.model.profile.Employee
import com.carservice.model.profile.Profile
import com.carservice.service.auth.AuthorizationService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate

data class SignupResponse(
    val profile: Profile,
    val accessToken: String? = null
)

@RestController
@RequestMapping("/api/auth")
class AuthorizationController(
    private val authService: AuthorizationService,
    private val restTemplate: RestTemplate
) {
    @Value("\${keycloak.credentials.secret}")
    private lateinit var clientSecret: String

    @PostMapping("/signup/customer")
    fun signupCustomer(@RequestBody request: Customer): ResponseEntity<SignupResponse> {
        val profile = authService.signup(request)
        val token = getInitialToken(profile.email, request.password)
        return ResponseEntity.ok(SignupResponse(profile, token))
    }

    @PostMapping("/signup/employee")
    fun signupEmployee(@RequestBody request: Employee): ResponseEntity<SignupResponse> {
        val profile = authService.signup(request)
        val token = getInitialToken(profile.email, request.password)
        return ResponseEntity.ok(SignupResponse(profile, token))
    }

    private fun getInitialToken(email: String, rawPassword: String): String? {
        val url = "http://localhost:8080/realms/car-service-realm/protocol/openid-connect/token"
        val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_FORM_URLENCODED }
        val body = "grant_type=password&client_id=car-service-client&client_secret=$clientSecret&username=$email&password=$rawPassword"
        val entity = HttpEntity(body, headers)
        val response = restTemplate.postForEntity(url, entity, Map::class.java)
        return response.body?.get("access_token") as? String
    }
}