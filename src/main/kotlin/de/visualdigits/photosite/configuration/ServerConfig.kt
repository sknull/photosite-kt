package de.visualdigits.photosite.configuration

import de.visualdigits.photosite.model.siteconfig.Photosite
import org.slf4j.LoggerFactory
import org.springframework.boot.web.server.Ssl
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("ssl")
class ServerConfig(
    private val photosite: Photosite
) : WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

    private val log = LoggerFactory.getLogger(ServerConfig::class.java)

    override fun customize(server: ConfigurableServletWebServerFactory) {
        log.info("rootDirectory: ${photosite.rootDirectory}")
        server.setPort(443)
        val ssl = Ssl()
        ssl.keyStore = "${photosite.rootDirectory}/secrets/keystore.p12"
        ssl.keyStoreType = "PKCS12"
        ssl.keyAlias = "springboot"
        ssl.keyStorePassword = "foodlyboo"
        server.setSsl(ssl)
    }
}
