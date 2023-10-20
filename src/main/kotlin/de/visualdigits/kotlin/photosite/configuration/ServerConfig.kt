package de.visualdigits.kotlin.photosite.configuration

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.server.Ssl
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("ssl")
open class ServerConfig : WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

    private val log = LoggerFactory.getLogger(ServerConfig::class.java)

    @Value("\${spring.resources.static-locations}")
    lateinit var rootDirectory: String

    override fun customize(server: ConfigurableServletWebServerFactory) {
        log.info("#### rootDirectory: $rootDirectory")
        server.setPort(443)
        val ssl = Ssl()
        ssl.keyStore = "$rootDirectory/keystore.p12"
        ssl.keyStoreType = "PKCS12"
        ssl.keyAlias = "springboot"
        ssl.keyStorePassword = "foodlyboo"
        server.setSsl(ssl)
    }
}
