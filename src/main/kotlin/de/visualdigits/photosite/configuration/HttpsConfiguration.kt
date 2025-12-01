package de.visualdigits.photosite.configuration

import de.visualdigits.photosite.model.photosite.Photosite
import org.apache.catalina.Context
import org.apache.catalina.connector.Connector
import org.apache.tomcat.util.descriptor.web.SecurityCollection
import org.apache.tomcat.util.descriptor.web.SecurityConstraint
import org.slf4j.LoggerFactory
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.server.Ssl
import org.springframework.boot.web.servlet.server.ServletWebServerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("ssl")
class HttpsConfiguration(
    private val photosite: Photosite
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Bean
    fun servletContainer(): ServletWebServerFactory? {
        log.info("rootDirectory: ${Photosite.rootDirectory}")

        val factory: TomcatServletWebServerFactory = object : TomcatServletWebServerFactory() {
            override fun postProcessContext(context: Context) {
                val securityConstraint = SecurityConstraint()
                securityConstraint.userConstraint = "CONFIDENTIAL"
                val collection = SecurityCollection()
                collection.addPattern("/*")
                securityConstraint.addCollection(collection)
                context.addConstraint(securityConstraint)
            }
        }

        factory.port = photosite.serverPort!!
        val ssl = Ssl()
        ssl.keyStore = photosite.ssl!!.keyStore!!
        ssl.keyStoreType = photosite.ssl!!.keyStoreType!!
        ssl.keyAlias = photosite.ssl!!.keyAlias!!
        ssl.keyStorePassword = photosite.ssl!!.keyStorePassword!!
        factory.ssl = ssl

        // redirect http to https
        val connector = Connector("org.apache.coyote.http11.Http11NioProtocol")
        connector.scheme = "http"
        connector.port = 80
        connector.secure = false
        connector.redirectPort = 443
        factory.addAdditionalTomcatConnectors(connector)

        return factory
    }
}
