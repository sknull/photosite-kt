package de.visualdigits.kotlin.photosite.model.rss

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

data class Channel(
    val title: String? = null,
    val link: String? = null,
    val description: String? = null,
    val language: String? = null,
    val copyright: String? = null,
    val generator: String? = null,
    val lastBuildDate: String? = null,
    @JacksonXmlProperty(localName = "item") @JacksonXmlElementWrapper(useWrapping = false, localName = "item") val items: List<Item>? = null
)
