package com.carservice.service.auth

import com.carservice.model.auth.UserSecurity
import com.carservice.repository.ProfileRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsService (
    private val profileRepository: ProfileRepository
): UserDetailsService {

    override fun loadUserByUsername(email: String): UserDetails {
        val profile = profileRepository.findByEmail(email)
            ?: throw UsernameNotFoundException("User not found with email: $email")

       return UserSecurity(
            id = profile.id,
            email = profile.email,
            password = profile.password,
            authorities = profile.role.map { SimpleGrantedAuthority(it.name) }.toMutableList()
        )
    }
}