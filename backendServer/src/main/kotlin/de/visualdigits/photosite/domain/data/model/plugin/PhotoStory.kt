package de.visualdigits.photosite.domain.data.model.plugin

import de.visualdigits.photosite.domain.data.model.page.content.ContentType

class PhotoStory(
    val mode: String? = null,
    val speed: Long = 0,
    val pause: Long = 0,
    val showThumbByDefault: Boolean = false,
    val animateThumb: Boolean = false,
    val progressBar: Boolean = false,
    val download: Boolean = false
) : Plugin(
    contentType = ContentType.PhotoStory
)
