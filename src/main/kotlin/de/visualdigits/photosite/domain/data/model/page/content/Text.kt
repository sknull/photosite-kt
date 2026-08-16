package de.visualdigits.photosite.domain.data.model.page.content

import de.visualdigits.photosite.domain.data.model.common.Language
import kotlinx.serialization.Serializable

@Serializable
data class Text(
    val lang: Language? = null,
    var value: String? = null
) {
    init {
        value = value?.trim()?.replace("\\n\\n +".toRegex(), "\n\n")
    }
}
