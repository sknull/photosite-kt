package de.visualdigits.photosite.model.page

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import de.visualdigits.photosite.model.common.Translation
import de.visualdigits.photosite.model.common.LanguageProvider


class Caption(
    @JacksonXmlProperty(isAttribute = true)
    val name: String? = null,

    @JacksonXmlProperty(isAttribute = true)
    val alt: String? = null,

    @JacksonXmlProperty(isAttribute = true)
    val caption: String? = null,

    i18n: List<Translation> = listOf()
) : LanguageProvider(i18n)

