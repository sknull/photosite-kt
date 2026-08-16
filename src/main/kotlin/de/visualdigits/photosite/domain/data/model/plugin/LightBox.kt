package de.visualdigits.photosite.domain.data.model.plugin

import de.visualdigits.photosite.domain.data.model.page.content.ContentType
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
    contentType = ContentType.LightBox
)
