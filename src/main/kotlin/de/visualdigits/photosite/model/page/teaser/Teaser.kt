package de.visualdigits.photosite.model.page.teaser

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty


class Teaser(
    @JacksonXmlProperty(localName = "googlemaps")
    val googleMaps: GoogleMaps? = null,

    val texts: List<Text>? = null
) {

    val i18nMap: MutableMap<String, Text> = mutableMapOf()

    init {
        texts?.forEach {
            i18nMap[it.lang!!] = it
        }
    }

    fun getHtml(language: String): String {
        val sb = StringBuilder()
        if (googleMaps != null) {
            sb.append(googleMaps.getHtml())
        }
        val text = i18nMap[language]
        if (text != null) {
            text.value?.trim()?.let { sb.append(it) }
        }
        return sb.toString()
    }
}

