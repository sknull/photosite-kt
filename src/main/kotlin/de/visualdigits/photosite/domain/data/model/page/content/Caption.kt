package de.visualdigits.photosite.domain.data.model.page.content

import de.visualdigits.photosite.domain.data.model.common.Language
import de.visualdigits.photosite.domain.data.model.common.Translation
import java.util.UUID

data class Caption(
    val id: UUID? = null,
    val name: String? = null,
    val alt: String? = null,
    val caption: String? = null,
    val translations: List<Translation> = listOf()
) {
    var translationsMap: Map<Language, Translation> = mapOf()

    init {
        translationsMap = translations.associateBy { t -> t.lang }
    }
}

