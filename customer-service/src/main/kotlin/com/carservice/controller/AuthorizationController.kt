package com.carservice.controller

import com.carservice.config.LoggerUtil
import com.carservice.dto.LoginRequest
import com.carservice.dto.LoginResponse
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
    private val authorizationService: AuthorizationService,
    private val restTemplate: RestTemplate
) {
    @Value("\${keycloak.oauth-client-secret}")
    private lateinit var clientSecret: String
    private val logger = LoggerUtil.getLogger(AuthorizationController::class.java)

    @PostMapping("/signup/customer")
    fun signupCustomer(@RequestBody customer: Customer): ResponseEntity<SignupResponse> {
        val profile = authorizationService.signup(customer)
        val token = getInitialToken(profile.email, customer.password)
        return ResponseEntity.ok(SignupResponse(profile, token))
    }

    @PostMapping("/signup/employee")
    fun signupEmployee(@RequestBody employee: Employee): ResponseEntity<SignupResponse> {
        val profile = authorizationService.signup(employee)
        val token = getInitialToken(profile.email, employee.password)
        return ResponseEntity.ok(SignupResponse(profile, token))
    }

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<LoginResponse> {
        val token = getInitialToken(loginRequest.email, loginRequest.password)
            ?: return ResponseEntity.status(401).build()

        return ResponseEntity.ok(LoginResponse(token, token))
    }

    private fun getInitialToken(email: String, rawPassword: String): String? {
        val url = "http://localhost:8080/realms/car-service-realm/protocol/openid-connect/token"
        val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_FORM_URLENCODED }
        val body = "grant_type=password&client_id=car-service-client&client_secret=$clientSecret&username=$email&password=$rawPassword"
        val entity = HttpEntity(body, headers)

        logger.debug("Attempting to obtain token for user: $email")
        return try {
            val response = restTemplate.postForEntity(url, entity, Map::class.java)
            if (response.statusCodeValue == 200) {
                val token = response.body?.get("access_token") as? String
                logger.info("Successfully obtained token for user: $email")
                token
            } else {
                logger.warn("Failed to obtain token for user $email, status: ${response.statusCode}, body: ${response.body}")
                null
            }
        } catch (e: Exception) {
            logger.error("Failed to obtain initial token for user $email: ${e.message}", e)
            null
        }
    }
}