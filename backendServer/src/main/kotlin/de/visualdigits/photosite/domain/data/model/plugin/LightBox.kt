package de.visualdigits.photosite.domain.data.model.plugin

import de.visualdigits.photosite.domain.data.model.page.content.ContentType
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "photosite.plugins.lightbox")
class LightBox(
    val resizeDuration: Long = 0,
    val fadeDuration: Long = 0,
    val imageFadeDuration: Long = 0,
    val wrapAround: Boolean = false
) : Plugin(
    contentType = ContentType.LightBox
)
