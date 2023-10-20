package de.visualdigits.kotlin.photosite.model.common

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

class Label(
    @JacksonXmlElementWrapper(useWrapping = false) @JacksonXmlProperty(localName = "lang") i18n: List<Language> = listOf(),
) : I18nProvider(i18n) {

    fun getTitle(language: String): String? {
        return i18nMap[language]?.let { lang -> lang.name?:lang.title }
    }
}
