package com.carservice

import com.carservice.config.JwtProperties
import com.carservice.config.SchedulerProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties(JwtProperties::class, SchedulerProperties::class)
class CarServiceApplication

fun main(args: Array<String>) {
    runApplication<CarServiceApplication>(*args)
}
