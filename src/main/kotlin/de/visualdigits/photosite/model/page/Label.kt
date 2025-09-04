package de.visualdigits.photosite.model.page

import com.fasterxml.jackson.annotation.JsonAlias
import java.util.Locale

class Label(
    @JsonAlias("lang", "i18n", "translations") var lang: List<Translation> = listOf(),
) {

    lateinit var translationsMap: Map<Locale, Translation>

    init {
        translationsMap = lang.associateBy { t -> t.lang!! }
    }

    fun getTitle(language: Locale): String? {
        return translationsMap[language]?.let { lang -> lang.name?:lang.title }
    }
}
