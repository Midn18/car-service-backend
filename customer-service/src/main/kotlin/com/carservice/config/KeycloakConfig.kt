package com.carservice.config

import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KeycloakConfig {

    @Value("\${keycloak.server-url}")
    private lateinit var serverUrl: String

    @Value("\${keycloak.admin-realm}")
    private lateinit var adminRealm: String

    @Value("\${keycloak.client-id}")
    private lateinit var clientId: String

    @Value("\${keycloak.admin-client-secret}")
    private lateinit var clientSecret: String

    @Bean
    fun keycloak(): Keycloak {
        return try {
            KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(adminRealm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType("client_credentials")
                .build()
        } catch (e: Exception) {
            throw RuntimeException("Failed to initialize Keycloak admin client: ${e.message}", e)
        }
    }
}