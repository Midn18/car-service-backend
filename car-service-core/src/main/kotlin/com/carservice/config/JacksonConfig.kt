package com.carservice.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JacksonConfig {

    @Bean
    fun objectMapper(): ObjectMapper {
        return StaticJackson.STATIC_OBJECT_MAPPER
    }
}

object StaticJackson {
    val STATIC_OBJECT_MAPPER = ObjectMapper().apply {
        registerModule(JavaTimeModule())
        registerModule(KotlinModule())
    }
}

fun Any.toJson(): String {
    return StaticJackson.STATIC_OBJECT_MAPPER.writeValueAsString(this)
}