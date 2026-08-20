package de.visualdigits.photosite.domain.data.model.page.content

import de.visualdigits.photosite.domain.data.model.common.Language
import java.util.UUID

data class Text(
    val id: UUID? = null,
    val lang: Language? = null,
    var value: String? = null
) {
    init {
        value = value?.trim()?.replace("\\n\\n +".toRegex(), "\n\n")
    }
}
