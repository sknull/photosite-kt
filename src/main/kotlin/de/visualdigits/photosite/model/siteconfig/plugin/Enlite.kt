package de.visualdigits.photosite.model.siteconfig.plugin

import de.visualdigits.photosite.model.pagemodern.ContentType
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "photosite.plugins.enlite")
class Enlite : Plugin(
    contentType = ContentType.Enlite
)
