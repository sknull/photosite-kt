package de.visualdigits.photosite.model.page.content

import kotlinx.serialization.Serializable

@Serializable
data class Image(
    val name: String? = null,
    val align: String? = null,
    val alt: String? = null
)
