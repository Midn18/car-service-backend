package com.carservice.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "scheduler.cron")
data class SchedulerProperties (
    val appointmentSlotGenerator: String
)