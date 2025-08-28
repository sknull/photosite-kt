package de.visualdigits.photosite.model.common

import de.visualdigits.photosite.model.page.Page
import de.visualdigits.photosite.model.siteconfig.Photosite

interface HtmlSnippet {

    fun getHead(photosite: Photosite): String {
        return ""
    }

    fun getHtml(
        photosite: Photosite,
        page: Page,
        language: String
    ): String
}
