package com.carservice.config

import com.auth0.client.auth.AuthAPI
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class Auth0Config {

    @Value("\${auth0.domain}")
    private lateinit var domain: String

    @Value("\${auth0.client-id}")
    private lateinit var clientId: String

    @Value("\${auth0.client-secret}")
    private lateinit var clientSecret: String

    @Bean
    fun authAPI(): AuthAPI = AuthAPI.newBuilder(domain, clientId, clientSecret).build()
}

