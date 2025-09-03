package de.visualdigits.photosite.model.siteconfig.plugin

import de.visualdigits.photosite.model.page.Page

class MarkdownContent : Plugin(
    name = "Markdwon"
) {

    override fun getHtml(page: Page, language: String): String {
        return ""
    }
}
