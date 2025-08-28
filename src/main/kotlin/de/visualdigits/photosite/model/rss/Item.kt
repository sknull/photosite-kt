package de.visualdigits.photosite.model.rss

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData
import java.time.OffsetDateTime


data class Item(
    val title: String? = null,
    val author: String? = null,
    val category: String? = null,
    val link: String? = null,
    val pubDate: OffsetDateTime? = null,
    @JacksonXmlCData val description: String? = null
)
