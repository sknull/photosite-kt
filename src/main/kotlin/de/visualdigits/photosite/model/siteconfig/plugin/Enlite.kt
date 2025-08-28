package de.visualdigits.photosite.model.siteconfig.plugin

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "photosite.plugins.enlite")
class Enlite : Plugin(
    name = "Enlite"
)
