package de.visualdigits.photosite.data.filesystem.model

import kotlinx.serialization.Serializable

@Serializable
data class CaptionDescriptor(
    val name: String? = null,
    val alt: String? = null,
    val caption: String? = null,
    val translations: List<TranslationDescriptor> = listOf()
)
