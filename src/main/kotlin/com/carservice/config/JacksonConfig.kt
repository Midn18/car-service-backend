package com.carservice.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration

@Configuration
class JacksonConfig(private val objectMapper: ObjectMapper) {

    @PostConstruct
    fun setUp() {
        objectMapper.registerModule(JavaTimeModule())
    }
}