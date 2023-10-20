package de.visualdigits.kotlin.photosite.model.siteconfig.plugin

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import de.visualdigits.kotlin.photosite.model.common.HtmlSnippet
import de.visualdigits.kotlin.photosite.model.page.Page
import de.visualdigits.kotlin.photosite.model.siteconfig.SiteConfigHolder

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "package")
@JsonSubTypes(
    JsonSubTypes.Type(value = LightGallery::class, name = "photosite.plugin.gallery.LightGallery"),
    JsonSubTypes.Type(value = Rotator::class, name = "photosite.plugin.gallery.Rotator"),

    JsonSubTypes.Type(value = LightBox::class, name = "photosite.plugin.gallery.LightBox"),
    JsonSubTypes.Type(value = PhotoStory::class, name = "photosite.plugin.gallery.PhotoStory"),
    JsonSubTypes.Type(value = Enlite::class, name = "photosite.plugin.lighting.Enlite"),
)
open class Plugin(
    val name: String,
    @JacksonXmlProperty(localName = "package") val clazz: String
) : HtmlSnippet {

    override fun getHtml(siteConfig: SiteConfigHolder, page: Page, language: String): String {
        val mdContent: String? = page.mdContent
        val htmlContent: String? = page.htmlContent
        return if (mdContent?.isNotBlank() == true) {
            mdContent
        } else if (htmlContent?.isNotBlank() == true) {
            htmlContent
        } else ""
    }
}
