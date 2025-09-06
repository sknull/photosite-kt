package de.visualdigits.photosite.model.page.content

import com.fasterxml.jackson.annotation.JsonAlias
import java.util.Locale


class Teaser(
    @JsonAlias("googlemaps", "googleMaps") val googleMaps: GoogleMaps? = null,
    val texts: List<Text> = listOf()
) {

    lateinit var translationsMap: Map<Locale, Text>

    init {
        translationsMap = texts.associate { t -> Pair(t.lang!!, t) }
    }

    fun getHtml(language: Locale): String {
        val sb = StringBuilder()
        if (googleMaps != null) {
            sb.append(googleMaps.getHtml())
        }
        val text = translationsMap[language]
        if (text != null) {
            text.value?.trim()?.let { sb.append(it) }
        }
        return sb.toString()
    }
}


