package de.visualdigits.photosite.data.filesystem.model

import kotlinx.serialization.Serializable

@Serializable
data class ImageDescriptor(
    val name: String? = null,
    val align: String? = null,
    val alt: String? = null
)
