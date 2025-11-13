package de.visualdigits.photosite.configuration

import de.visualdigits.photosite.model.photosite.Photosite
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter

@Configuration
class InternalApiAccessFilterConfiguration(
    private val photosite: Photosite
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .addFilterAfter(
                InternalApiAccessFilterBean(photosite.internalPort!!, photosite.internalEndpoints),
                BasicAuthenticationFilter::class.java
            )
            .build()
    }
}