package de.visualdigits.photosite.presentation.model

import kotlinx.serialization.Serializable

@Serializable
data class CaptionDto(
    val name: String? = null,
    val alt: String? = null,
    val caption: String? = null,
    val translations: List<TranslationDto> = listOf()
)
