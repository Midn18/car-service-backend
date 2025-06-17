package com.carservice.config

import com.carservice.security.JwtAuthenticationFilter
import com.carservice.security.JwtAuthorizationFilter
import com.carservice.security.JwtTokenUtil
import com.carservice.service.auth.UserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val userDetailsService: UserDetailsService,
    jwtProperties: JwtProperties
) {
    private val jwtTokenUtil: JwtTokenUtil = JwtTokenUtil(jwtProperties)

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationManager(http: HttpSecurity): AuthenticationManager {
        val authBuilder = http.getSharedObject(AuthenticationManagerBuilder::class.java)
        authBuilder
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder())
        return authBuilder.build()
    }

    @Bean
    fun filterChain(
        http: HttpSecurity,
        authenticationManager: AuthenticationManager
    ): SecurityFilterChain {
        val jwtAuthenticationFilter = JwtAuthenticationFilter(jwtTokenUtil, authenticationManager)
        jwtAuthenticationFilter.setFilterProcessesUrl("/api/auth/login")

        val jwtAuthorizationFilter = JwtAuthorizationFilter(jwtTokenUtil, userDetailsService, authenticationManager)

        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/api/auth/**"
                ).permitAll()
                it.anyRequest().authenticated()
            }
            .addFilter(jwtAuthenticationFilter)
            .addFilter(jwtAuthorizationFilter)

        return http.build()
    }
}