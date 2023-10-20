package de.visualdigits.kotlin.photosite.model.page.teaser

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

class Text(
    @JacksonXmlProperty(isAttribute = true)
    val lang: String? = null,

    @JacksonXmlCData
    val value: String? = null
)

