package de.visualdigits.kotlin.photosite.model.siteconfig.plugin

class PhotoStory(
    val mode: String? = null,
    val speed: Long = 0,
    val pause: Long = 0,
    val showThumbByDefault: Boolean = false,
    val animateThumb: Boolean = false,
    val progressBar: Boolean = false,
    val download: Boolean = false
) : Plugin(
name = "PhotoStory",
clazz = "de.visualdigits.kotlin.photosite.model.siteconfig.plugin.PhotoStory"
)
