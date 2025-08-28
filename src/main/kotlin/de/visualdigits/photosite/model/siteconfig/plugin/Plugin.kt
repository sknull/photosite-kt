package de.visualdigits.photosite.model.siteconfig.plugin

import de.visualdigits.photosite.model.common.HtmlSnippet
import de.visualdigits.photosite.model.page.Page
import de.visualdigits.photosite.model.siteconfig.Photosite

abstract class Plugin(
    val name: String = ""
) : HtmlSnippet {

    override fun getHtml(photosite: Photosite, page: Page, language: String): String {
        val mdContent: String? = page.mdContent
        val htmlContent: String? = page.htmlContent
        return if (mdContent?.isNotBlank() == true) {
            mdContent
        } else if (htmlContent?.isNotBlank() == true) {
            htmlContent
        } else ""
    }
}
