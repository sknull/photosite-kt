package de.visualdigits.photosite.model.common

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

class Language(
    @JacksonXmlProperty(isAttribute = true) var lang: String? = null,
    @JacksonXmlProperty(isAttribute = true) val alt: String? = null,
    @JacksonXmlProperty(isAttribute = true) val name: String? = null,
    @JacksonXmlProperty(isAttribute = true) val title: String? = null,
)
