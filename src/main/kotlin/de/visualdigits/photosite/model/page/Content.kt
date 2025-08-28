package de.visualdigits.photosite.model.page

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import de.visualdigits.photosite.model.common.HtmlSnippet
import de.visualdigits.photosite.model.common.sort.Sort
import de.visualdigits.photosite.model.page.teaser.Teaser
import de.visualdigits.photosite.model.siteconfig.Photosite

class Content(
    @JacksonXmlProperty(isAttribute = true) var plugin: String? = null,
    @JacksonXmlProperty(isAttribute = true) val mode: String? = null,
    @JacksonXmlProperty(isAttribute = true) val speed: Long = 0L,
    @JacksonXmlProperty(isAttribute = true) val pause: Long = 0L,
    @JacksonXmlProperty(isAttribute = true) val showThumbByDefault: Boolean = false,
    @JacksonXmlProperty(isAttribute = true) val animateThumb: Boolean = false,
    @JacksonXmlProperty(isAttribute = true) val progressBar: Boolean = false,
    @JacksonXmlProperty(isAttribute = true) val download: Boolean = false,
    val sort: Sort? = null,
    val teaser: Teaser? = null,
    val captions: List<Caption>? = null,
    @JsonIgnore val captionsMap: Map<String, Caption> = mapOf(),
    @JacksonXmlProperty(localName = "itemtype") val itemType: String? = null,
    val keywords: String? = null,
    val paragraphs: List<Paragraph>? = null
) : HtmlSnippet {

    override fun getHead(photosite: Photosite): String {
        return plugin?.let {
            photosite.getPluginConfig(it)
                ?.getHead(photosite)
                ?:""
        }?:""
    }

    override fun getHtml(photosite: Photosite, page: Page, language: String): String {
        return plugin?.let {
            photosite.getPluginConfig(it)
                ?.getHtml(photosite, page, language)
                ?:""
        }?:""
    }
}
