package com.carservice.service

import com.carservice.config.JwtProperties
import com.carservice.dto.authorization.CustomerSignupRequest
import com.carservice.dto.authorization.EmployeeSignupRequest
import com.carservice.dto.authorization.LoginRequest
import com.carservice.model.profile.*
import com.carservice.repository.ProfileRepository
import org.springframework.context.annotation.Bean
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*
import java.util.UUID.randomUUID

@Service
class AuthorizationService(
    val profileRepository: ProfileRepository,
    val tokenService: TokenService,
    val jwtProperties: JwtProperties
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    fun signupCustomer(request: CustomerSignupRequest): Profile {
        val customer = Customer(
            id = UUID(randomUUID().mostSignificantBits, randomUUID().leastSignificantBits),
            role = setOf(UserRole.GUEST),
            firstName = request.firstName,
            lastName = request.lastName,
            email = request.email,
            password = passwordEncoder().encode(request.password),
            phoneNumber = request.phoneNumber,
            dateOfBirth = request.dateOfBirth,
            address = request.address,
            serviceVisits = emptyList()
        )
        return profileRepository.save(customer)
    }

    fun signupEmployee(request: EmployeeSignupRequest): Profile {
        if (request.role.any { !(it.isEmployeeRole() || it.isAdmin()) }) {
            throw IllegalArgumentException("Invalid role for employee")
        }

        val employee = Employee(
            id = UUID(randomUUID().mostSignificantBits, randomUUID().leastSignificantBits),
            firstName = request.firstName,
            lastName = request.lastName,
            email = request.email,
            password = passwordEncoder().encode(request.password),
            phoneNumber = request.phoneNumber,
            dateOfBirth = request.dateOfBirth,
            address = request.address,
            role = request.role
        )
        return profileRepository.save(employee)
    }

    fun login(request: LoginRequest): String {
        val user = profileRepository.findByEmail(request.email)
            ?: throw IllegalArgumentException("Email not registered")

        if (!passwordEncoder().matches(request.password, user.password)) {
            throw IllegalArgumentException("Invalid credentials")
        }

        val userDetails = User(
            user.email,
            user.password,
            user.role.map { SimpleGrantedAuthority(it.name) }
        )

        return tokenService.generate(
            userDetails = userDetails,
            expirationDate = Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration),
            additionalClaims = mapOf("roles" to user.role.map { it.name })
        )
    }
}