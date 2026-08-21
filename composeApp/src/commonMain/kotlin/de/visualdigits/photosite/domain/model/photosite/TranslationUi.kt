package de.visualdigits.photosite.domain.model.photosite

import de.visualdigits.photosite.domain.model.type.Language
import kotlinx.serialization.Serializable

@Serializable
data class TranslationUi(
    val lang: Language = Language.DE,
    val name: String? = null,
    val alt: String? = null,
    val title: String? = null
)
