package de.visualdigits.photosite.presentation.model

import de.visualdigits.photosite.domain.data.model.common.Language
import kotlinx.serialization.Serializable

@Serializable
data class TranslationDto(
    val lang: Language = Language.DE,
    val name: String? = null,
    val alt: String? = null,
    val title: String? = null
)
