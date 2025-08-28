package de.visualdigits.photosite.model.common

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

class Link(
    @JacksonXmlProperty(isAttribute = true) val href: String? = null,
    val label: Label
)
