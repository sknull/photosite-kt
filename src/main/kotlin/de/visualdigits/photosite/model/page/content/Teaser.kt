package de.visualdigits.photosite.model.page.content

import de.visualdigits.photosite.model.common.Language
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
class Teaser(
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
        val text = translationsMap[language]
        text?.value?.trim()?.let { sb.append(it) }
        return sb.toString()
    }
}


