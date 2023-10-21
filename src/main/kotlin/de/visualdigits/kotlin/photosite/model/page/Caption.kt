package de.visualdigits.kotlin.photosite.model.page

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import de.visualdigits.kotlin.photosite.model.common.I18nProvider
import de.visualdigits.kotlin.photosite.model.common.Language


class Caption(
    @JacksonXmlProperty(isAttribute = true)
    val name: String? = null,

    @JacksonXmlProperty(isAttribute = true)
    val alt: String? = null,

    @JacksonXmlProperty(isAttribute = true)
    val caption: String? = null,

    i18n: List<Language> = listOf()
) : I18nProvider(i18n)

