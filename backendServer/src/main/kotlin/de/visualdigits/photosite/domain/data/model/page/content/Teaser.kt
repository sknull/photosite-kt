package de.visualdigits.photosite.domain.data.model.page.content

import de.visualdigits.photosite.domain.data.model.common.Language
import java.util.UUID


data class Teaser(
    val id: UUID? = null,
    val googleMaps: GoogleMaps? = null,
    val texts: List<Text> = listOf()
) {
    var translationsMap: Map<Language, Text> = texts.associateBy { t -> t.lang!! }

    fun getHtml(language: Language): String {
        val sb = StringBuilder()
        if (googleMaps != null) {
            sb.append(googleMaps.html)
        }
        translationsMap[language]?.value
            ?.trim()
            ?.replace("\\\n", "<br/>")
            ?.replace("\n", "<br/>")
            ?.also { sb.append(it) }
        return sb.toString()
    }
}


