package de.visualdigits.photosite.domain.model.photosite

import kotlinx.serialization.Serializable

@Serializable
data class ImageUi(
    val name: String? = null,
    val align: String? = null,
    val alt: String? = null
)
