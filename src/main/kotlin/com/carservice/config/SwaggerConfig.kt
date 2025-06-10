    package com.carservice.config

    import org.springframework.context.annotation.Bean
    import org.springframework.context.annotation.Configuration
    import io.swagger.v3.oas.models.OpenAPI
    import io.swagger.v3.oas.models.info.Info

    @Configuration
    class SwaggerConfig {

        @Bean
        fun openApiConfig(): OpenAPI {
            return OpenAPI()
                .info(
                    Info()
                        .title("Car Service API")
                        .version("1.0.0")
                        .description("API documentation for the Car Service application")
                )
        }
    }
