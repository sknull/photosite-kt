package de.visualdigits.photosite.model.plugin

import de.visualdigits.photosite.model.common.Language
import de.visualdigits.photosite.model.page.Page
import de.visualdigits.photosite.model.page.content.ContentType
import de.visualdigits.photosite.service.ImageService

abstract class Plugin(
    val contentType: ContentType
) {

    open fun getHead(theme: String): String = ""

    open fun renderHtml(page: Page, language: Language, imageService: ImageService): String {
        val mdContent: String? = page.content.mdContent
        val htmlContent: String? = page.content.htmlContent
        return if (mdContent?.isNotBlank() == true) {
            "\n$mdContent"
        } else if (htmlContent?.isNotBlank() == true) {
            "\n$htmlContent"
        } else ""
    }
}
