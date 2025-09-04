package de.visualdigits.photosite.model.common

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import java.util.Locale

class Translation(
    @JacksonXmlProperty(isAttribute = true) var lang: Locale? = null,
    @JacksonXmlProperty(isAttribute = true) val alt: String? = null,
    @JacksonXmlProperty(isAttribute = true) val name: String? = null,
    @JacksonXmlProperty(isAttribute = true) val title: String? = null,
)
