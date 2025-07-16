package com.carservice

import com.carservice.config.JwtProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties::class)
class CarServiceApplication

fun main(args: Array<String>) {
	runApplication<CarServiceApplication>(*args)
}
