package de.visualdigits.photosite.model.common

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper

class Label(
    @JacksonXmlElementWrapper(useWrapping = false) lang: List<Language> = listOf(),
) : LanguageProvider(lang) {

    fun getTitle(language: String): String? {
        return languageMap[language]?.let { lang -> lang.name?:lang.title }
    }
}
