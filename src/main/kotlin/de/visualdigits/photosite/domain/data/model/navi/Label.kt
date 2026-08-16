package de.visualdigits.photosite.domain.data.model.navi

import de.visualdigits.photosite.domain.data.model.common.Language
import de.visualdigits.photosite.domain.data.model.common.Translation

class Label(
    var lang: List<Translation> = listOf(),
) {

    var translationsMap: Map<Language, Translation>

    init {
        translationsMap = lang.associateBy { t -> t.lang!! }
    }

    fun getTitle(language: Language): String? {
        return translationsMap[language]?.let { lang -> lang.name?:lang.title }
    }
}
