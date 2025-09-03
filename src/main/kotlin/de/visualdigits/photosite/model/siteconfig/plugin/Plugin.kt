package de.visualdigits.photosite.model.siteconfig.plugin

import de.visualdigits.photosite.model.page.Page

abstract class Plugin(
    val name: String = ""
) {

    open fun getHead(theme: String): String = ""

    open fun getHtml(page: Page, language: String): String {
        val mdContent: String? = page.content.mdContent
        val htmlContent: String? = page.content.htmlContent
        return if (mdContent?.isNotBlank() == true) {
            mdContent
        } else if (htmlContent?.isNotBlank() == true) {
            htmlContent
        } else ""
    }
}
