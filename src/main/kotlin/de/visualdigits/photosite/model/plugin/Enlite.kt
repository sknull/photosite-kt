package de.visualdigits.photosite.model.plugin

import de.visualdigits.photosite.model.page.content.ContentType
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "photosite.plugins.enlite")
class Enlite : Plugin(
    contentType = ContentType.Enlite
)
