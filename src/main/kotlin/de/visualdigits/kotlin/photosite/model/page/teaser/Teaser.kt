package de.visualdigits.kotlin.photosite.model.page.teaser

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import de.visualdigits.kotlin.photosite.model.common.HtmlSnippet
import de.visualdigits.kotlin.photosite.model.page.Page
import de.visualdigits.kotlin.photosite.model.siteconfig.SiteConfig


class Teaser(
    @JacksonXmlProperty(localName = "googlemaps")
    val googleMaps: GoogleMaps? = null,

    val texts: List<Text>? = null
) : HtmlSnippet {

    val i18nMap: MutableMap<String, Text> = mutableMapOf()

    init {
        texts?.forEach {
            i18nMap[it.lang!!] = it
        }
    }

    override fun getHtml(siteConfig: SiteConfig, page: Page, language: String): String {
        val sb = StringBuilder()
        if (googleMaps != null) {
            sb.append(googleMaps.getHtml(siteConfig, page, language))
        }
        val text = i18nMap[language]
        if (text != null) {
            text.value?.trim()?.let { sb.append(it) }
        }
        return sb.toString()
    }
}

