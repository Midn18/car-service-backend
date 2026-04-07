package com.carservice.service.auth

import com.carservice.model.auth.UserSecurity
import com.carservice.repository.ProfileRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class JwtUserDetailsMapper(
    private val profileRepository: ProfileRepository
) {
    @Value("\${auth0.claims-namespace:https://carservice.com}")
    private lateinit var claimsNamespace: String

    fun createUserDetailsFromJwt(jwt: Jwt): UserSecurity {
        val sub = jwt.subject ?: throw UsernameNotFoundException("No subject claim in JWT")

        // 1. Try roles embedded in the access token via Auth0 Action
        val rolesFromToken: List<String>? = jwt.getClaimAsStringList("$claimsNamespace/roles")

        val authorities = if (!rolesFromToken.isNullOrEmpty()) {
            rolesFromToken.map { SimpleGrantedAuthority("ROLE_$it") }
        } else {
            // 2. Fallback: load roles from MongoDB profile (works without Auth0 Action)
            profileRepository.findById(sub).orElse(null)
                ?.role?.map { SimpleGrantedAuthority("ROLE_${it.name}") }
                ?: emptyList()
        }

        // Email: prefer custom claim, then standard claim, then sub
        val email = jwt.getClaimAsString("$claimsNamespace/email")
            ?: jwt.getClaimAsString("email")
            ?: sub

        return UserSecurity(
            id = UUID.nameUUIDFromBytes(sub.toByteArray()),
            email = email,
            password = "",
            authorities = authorities.toMutableList()
        )
    }
}