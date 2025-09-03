package de.visualdigits.photosite.model.siteconfig.plugin

import de.visualdigits.photosite.model.page.Page

class HtmlContent : Plugin(
    name = "Html"
) {

    override fun getHtml(page: Page, language: String): String {
        return ""
    }
}
