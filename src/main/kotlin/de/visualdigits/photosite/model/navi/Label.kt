package de.visualdigits.photosite.model.navi

import de.visualdigits.photosite.model.common.Translation
import java.util.Locale

class Label(
    var lang: List<Translation> = listOf(),
) {

    lateinit var translationsMap: Map<Locale, Translation>

    init {
        translationsMap = lang.associateBy { t -> t.lang!! }
    }

    fun getTitle(language: Locale): String? {
        return translationsMap[language]?.let { lang -> lang.name?:lang.title }
    }
}
