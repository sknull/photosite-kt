package de.visualdigits.kotlin.photosite.model.common

import de.visualdigits.kotlin.photosite.model.page.Page
import de.visualdigits.kotlin.photosite.model.siteconfig.SiteConfig

interface HtmlSnippet {

    fun getHead(siteConfig: SiteConfig): String {
        return ""
    }

    fun getHtml(
        siteConfig: SiteConfig,
        page: Page, language: String
    ): String
}
