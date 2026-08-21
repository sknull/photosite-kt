package de.visualdigits.photosite.presentation.model

import de.visualdigits.photosite.domain.data.model.common.Language
import kotlinx.serialization.Serializable

@Serializable
data class TextDto(
    val lang: Language? = null,
    var value: String? = null
)
