package de.visualdigits.photosite.domain.data.model.page.content

import de.visualdigits.photosite.domain.data.model.common.Language
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
data class Teaser(
   val googleMaps: GoogleMaps? = null,
   val texts: List<Text> = listOf()
) {

    @Transient
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


