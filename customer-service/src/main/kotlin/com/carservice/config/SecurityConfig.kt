package com.carservice.config

import com.carservice.service.auth.JwtUserDetailsMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfig(
    private val jwtUserDetailsMapper: JwtUserDetailsMapper
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
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
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt { jwt ->
                    jwt.decoder(jwtDecoder())
                    jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())
                }
            }

        return http.build()
    }

    @Bean
    fun jwtDecoder(): JwtDecoder {
        val issuerUri = "http://localhost:8080/realms/car-service-realm" // Pune în @Value dacă vrei din config
        return NimbusJwtDecoder.withJwkSetUri("$issuerUri/protocol/openid-connect/certs").build()
    }

    @Bean
    fun jwtAuthenticationConverter(): JwtAuthenticationConverter {
        val converter = JwtAuthenticationConverter()
        converter.setJwtGrantedAuthoritiesConverter { jwt ->
            val userDetails = jwtUserDetailsMapper.createUserDetailsFromJwt(jwt)
            userDetails.authorities
        }
        return converter
    }

    fun isEmployee(authentication: Authentication): Boolean {
        val roles = authentication.authorities.map { it.authority.removePrefix("ROLE_") }
        return roles.any { role ->
            role in listOf("MECHANIC", "CAR_DETAILER", "CAR_PAINTER", "ELECTRICIAN", "ADMIN")
        }
    }
}