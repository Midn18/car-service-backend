package com.carservice.service.auth

import com.carservice.model.auth.UserSecurity
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class JwtUserDetailsMapper {
    fun createUserDetailsFromJwt(jwt: Jwt): UserSecurity {
        val rolesClaim = jwt.getClaimAsMap("realm_access")
        val rolesList = rolesClaim?.get("roles") as? List<*>

        val roles = rolesList?.mapNotNull { role ->
            when (role) {
                is String -> SimpleGrantedAuthority("ROLE_$role")
                else -> null
            }
        } ?: emptyList()

        return UserSecurity(
            id = UUID.fromString(jwt.subject ?: throw UsernameNotFoundException("No subject in JWT")),
            email = jwt.getClaimAsString("email") ?: throw UsernameNotFoundException("No email in JWT"),
            password = "",
            authorities = roles.toMutableList()
        )
    }
}