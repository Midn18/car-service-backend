package com.carservice.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory

object LoggerUtil {
    private val logger: Logger = LoggerFactory.getLogger(LoggerUtil::class.java)

    fun getLogger(clazz: Class<*>): Logger = LoggerFactory.getLogger(clazz)

    inline fun <reified T> getLogger(): Logger = LoggerFactory.getLogger(T::class.java)
}