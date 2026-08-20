package de.visualdigits.photosite.presentation.model

import kotlinx.serialization.Serializable

@Serializable
data class ImageDto(
    val name: String? = null,
    val align: String? = null,
    val alt: String? = null
)
