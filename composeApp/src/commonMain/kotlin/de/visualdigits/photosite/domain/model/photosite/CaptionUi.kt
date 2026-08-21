package de.visualdigits.photosite.domain.model.photosite

import kotlinx.serialization.Serializable

@Serializable
data class CaptionUi(
    val name: String? = null,
    val alt: String? = null,
    val caption: String? = null,
    val translations: List<TranslationUi> = listOf()
)
