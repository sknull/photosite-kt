package de.visualdigits.photosite.domain.model.photosite

import de.visualdigits.common.domain.model.common.KmpOffsetDateTime
import de.visualdigits.photosite.domain.model.type.Language
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class PageUi(
    val icon: String? = null,
    val content: ContentUi? = null,
    val translations: List<TranslationUi> = listOf(),
    val name: String = "",
    val path: String = "/",
    val children: List<PageUi> = listOf(),
    val lastModified: KmpOffsetDateTime? = null,
) {

    @Transient var parent: PageUi? = null
    @Transient val translationMap: Map<Language, TranslationUi> = translations.associateBy { it.lang }

    override fun toString(): String = path
}
