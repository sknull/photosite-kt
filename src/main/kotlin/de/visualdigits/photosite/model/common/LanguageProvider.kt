package de.visualdigits.photosite.model.common

import com.fasterxml.jackson.annotation.JsonAlias
import java.util.Locale

open class LanguageProvider(
    @JsonAlias("lang", "translations") var translations: List<Translation> = listOf(),
) {

    val translationsMap: Map<Locale, Translation> = translations
        .associate { t -> Pair(t.lang!!, t) }
}
