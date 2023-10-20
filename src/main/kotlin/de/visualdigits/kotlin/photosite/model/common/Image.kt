package de.visualdigits.kotlin.photosite.model.common

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty


class Image(
    @JacksonXmlProperty(isAttribute = true)
    val name: String? = null,

    @JacksonXmlProperty(isAttribute = true)
    val align: String? = null,

    @JacksonXmlProperty(isAttribute = true)
    val alt: String? = null
)
