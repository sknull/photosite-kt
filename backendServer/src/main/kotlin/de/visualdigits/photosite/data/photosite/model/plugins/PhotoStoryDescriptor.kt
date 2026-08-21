package de.visualdigits.photosite.data.photosite.model.plugins

import kotlinx.serialization.Serializable

@Serializable
data class PhotoStoryDescriptor(
    val mode: String? = null,
    val speed: Long = 0,
    val pause: Long = 0,
    val showThumbByDefault: Boolean = false,
    val animateThumb: Boolean = false,
    val progressBar: Boolean = false,
    val download: Boolean = false
)
