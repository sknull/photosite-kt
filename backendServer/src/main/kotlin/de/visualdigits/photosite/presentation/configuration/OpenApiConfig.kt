package de.visualdigits.photosite.presentation.configuration

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

/**
 * Configuration for open api docs aka swagger.
 */
@Configuration
@Profile("!prod")
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Photosite API")
                    .description("Automated API-Documentation for the photosite.")
                    .version("1.0.0")
            )
    }
}
