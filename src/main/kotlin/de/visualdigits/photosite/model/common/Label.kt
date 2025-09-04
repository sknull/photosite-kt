package de.visualdigits.photosite.model.common

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import java.util.Locale

class Label(
    @JsonAlias("label", "translations") @JacksonXmlElementWrapper(useWrapping = false) translations: List<Translation> = listOf(),
) : LanguageProvider(translations) {

    fun getTitle(language: Locale): String? {
        return translationsMap[language]?.let { lang -> lang.name?:lang.title }
    }
}
