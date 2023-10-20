package de.visualdigits.kotlin.photosite.model.page

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import de.visualdigits.kotlin.photosite.model.common.HtmlSnippet
import de.visualdigits.kotlin.photosite.model.common.sort.Sort
import de.visualdigits.kotlin.photosite.model.page.teaser.Teaser
import de.visualdigits.kotlin.photosite.model.siteconfig.SiteConfigHolder

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
    @JsonIgnore val captionsMap: Map<String, Caption> = HashMap<String, Caption>(),
    @JacksonXmlProperty(localName = "itemtype") val itemType: String? = null,
    val keywords: String? = null,
    val paragraphs: List<Paragraph>? = null
) : HtmlSnippet {

    override fun getHead(siteConfig: SiteConfigHolder): String {
        return plugin?.let {
            siteConfig.getPluginConfig(it)
                ?.getHead(siteConfig)
                ?:""
        }?:""
    }

    override fun getHtml(siteConfig: SiteConfigHolder, page: Page, language: String): String {
        return plugin?.let {
            siteConfig.getPluginConfig(it)
                ?.getHtml(siteConfig, page, language)
                ?:""
        }?:""
    }
}
