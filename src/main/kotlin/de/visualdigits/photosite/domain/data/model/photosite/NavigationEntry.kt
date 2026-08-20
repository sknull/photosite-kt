package de.visualdigits.photosite.domain.data.model.photosite

import de.visualdigits.photosite.domain.data.model.common.Language
import de.visualdigits.photosite.domain.data.model.common.Translation

class NavigationEntry(
    val rootFolder: String? = null,
    val numberOfEntries: Int = 0,
    val translations: List<Translation> = listOf()
){

    val translationsMap: Map<Language, Translation> = translations.associateBy { it.lang }

    fun getTitle(language: Language): String? {
        return translationsMap[language]?.let { lang -> lang.name?:lang.title }
    }
}
