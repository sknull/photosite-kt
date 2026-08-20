package de.visualdigits.photosite.domain.data.model.page.content

import de.visualdigits.photosite.domain.data.model.common.Language
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.UUID

@Serializable
data class Text(
    @Transient val id: UUID? = null,
    val lang: Language? = null,
    var value: String? = null
) {
    init {
        value = value?.trim()?.replace("\\n\\n +".toRegex(), "\n\n")
    }
}
