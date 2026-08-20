package de.visualdigits.photosite.data.filesystem.model

import kotlinx.serialization.Serializable

@Serializable
data class PageDescriptor(
    val icon: String? = null,
    val tocName: String? = null,
    val content: ContentDescriptor = ContentDescriptor(),
    val translations: List<TranslationDescriptor> = listOf(),
)
