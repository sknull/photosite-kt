package de.visualdigits.photosite.data.filesystem.model

import de.visualdigits.photosite.domain.data.model.common.Language
import kotlinx.serialization.Serializable

@Serializable
data class TextDescriptor(
    val lang: Language? = null,
    var value: String? = null
)
