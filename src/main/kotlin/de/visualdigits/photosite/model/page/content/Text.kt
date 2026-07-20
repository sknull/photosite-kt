package de.visualdigits.photosite.model.page.content

import de.visualdigits.photosite.model.common.Language
import kotlinx.serialization.Serializable

@Serializable
class Text(
    val lang: Language? = null,
    var value: String? = null
) {
    init {
        value = value?.trim()?.replace("\\n\\n +".toRegex(), "\n\n")
    }
}
