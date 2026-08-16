package de.visualdigits.photosite.domain.data.model.page.content

import de.visualdigits.photosite.domain.data.model.common.Language
import de.visualdigits.photosite.domain.data.model.common.Translation
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Caption(
    val name: String? = null,
    val alt: String? = null,
    val caption: String? = null,
    val translations: List<Translation> = listOf()
) {

    @Transient
    var translationsMap: Map<Language, Translation> = mapOf()

    init {
        translationsMap = translations.associateBy { t -> t.lang!! }
    }
}

