package com.carservice.config

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer

@Configuration
class KafkaConfig {

    @Value("\${spring.kafka.bootstrap-servers}")
    private lateinit var bootstrapServers: String

    @Bean
    fun consumerFactory(): ConsumerFactory<String, Any> {
        val config = mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ConsumerConfig.GROUP_ID_CONFIG to "employee-service-group",
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java,
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to false,
            ConsumerConfig.RECONNECT_BACKOFF_MS_CONFIG to 1000,
            ConsumerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG to 10000,
            ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG to 60000,
            ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG to 45000,
            ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG to 3000,
            JsonDeserializer.TRUSTED_PACKAGES to "com.carservice.event",
            "spring.json.value.type.mapping" to "employeeCreated:com.carservice.event.EmployeeCreatedEvent",
            "spring.json.value.type.headers.enabled" to true,
            JsonDeserializer.VALUE_DEFAULT_TYPE to "java.lang.Object"
        )
        return DefaultKafkaConsumerFactory(config)
    }

    @Bean
    fun kafkaListenerContainerFactory(
        consumerFactory: ConsumerFactory<String, Any>
    ): ConcurrentKafkaListenerContainerFactory<String, Any> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, Any>()
        factory.consumerFactory = consumerFactory
        return factory
    }
}