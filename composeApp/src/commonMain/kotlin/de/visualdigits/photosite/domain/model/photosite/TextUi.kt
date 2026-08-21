package de.visualdigits.photosite.domain.model.photosite

import de.visualdigits.photosite.domain.model.type.Language
import kotlinx.serialization.Serializable

@Serializable
data class TextUi(
    val lang: Language? = null,
    var value: String? = null
)
