package de.visualdigits.photosite.model.siteconfig.plugin

import de.visualdigits.photosite.model.pagemodern.ContentType
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "photosite.plugins.photostory")
class PhotoStory(
    var mode: String? = null,
    var speed: Long = 0,
    var pause: Long = 0,
    var showThumbByDefault: Boolean = false,
    var animateThumb: Boolean = false,
    var progressBar: Boolean = false,
    var download: Boolean = false
) : Plugin(
    contentType = ContentType.PhotoStory
)
