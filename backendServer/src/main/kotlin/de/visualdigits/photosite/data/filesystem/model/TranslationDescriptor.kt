package de.visualdigits.photosite.data.filesystem.model

import de.visualdigits.photosite.domain.data.model.common.Language
import kotlinx.serialization.Serializable

@Serializable
data class TranslationDescriptor(
    val lang: Language = Language.DE,
    val name: String? = null,
    val alt: String? = null,
    val title: String? = null
)
