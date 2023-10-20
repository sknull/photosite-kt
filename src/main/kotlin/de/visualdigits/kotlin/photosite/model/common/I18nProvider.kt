package de.visualdigits.kotlin.photosite.model.common

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import de.visualdigits.kotlin.photosite.model.siteconfig.SiteConfig

@JsonIgnoreProperties("i18nMap")
open class I18nProvider(
    var i18n: List<Language> = listOf(),
) {

    val i18nMap: MutableMap<String, Language> = mutableMapOf()

    init {
        initialize()
    }

    fun initialize() {
        i18n.forEach {
            i18nMap[it.lang ?: throw IllegalArgumentException("No language given")] = it
        }
    }

    fun getI18n(language: String): Language? {
        return i18nMap[language]
    }

    fun getI18n(siteConfig: SiteConfig, language: String): Language? {
        var lang: Language? = i18nMap[language]
        if (lang == null) {
            lang = i18nMap[siteConfig.site.languageDefault]
        }
        return lang
    }
}
