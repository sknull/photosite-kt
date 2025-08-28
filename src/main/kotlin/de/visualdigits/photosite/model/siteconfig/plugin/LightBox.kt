package de.visualdigits.photosite.model.siteconfig.plugin

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "photosite.plugins.lightbox")
class LightBox(
    var resizeDuration: Long = 0,
    var fadeDuration: Long = 0,
    var imageFadeDuration: Long = 0,
    var wrapAround: Boolean = false
) : Plugin(
    name = "LightBox"
)
