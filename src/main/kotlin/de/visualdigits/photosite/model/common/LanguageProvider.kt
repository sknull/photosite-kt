package de.visualdigits.photosite.model.common

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties("i18nMap")
open class LanguageProvider(
    var lang: List<Language> = listOf(),
) {

    val languageMap: MutableMap<String, Language> = mutableMapOf()

    init {
        initialize()
    }

    fun initialize() {
        lang.forEach {
            languageMap[it.lang ?: throw IllegalArgumentException("No language given")] = it
        }
    }

    fun getTranslation(language: String): Language? {
        return languageMap[language]
    }
}
