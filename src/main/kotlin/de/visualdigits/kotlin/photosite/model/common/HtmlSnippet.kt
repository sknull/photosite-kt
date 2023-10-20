package de.visualdigits.kotlin.photosite.model.common

import de.visualdigits.kotlin.photosite.model.page.Page
import de.visualdigits.kotlin.photosite.model.siteconfig.SiteConfigHolder

interface HtmlSnippet {

    fun getHead(siteConfig: SiteConfigHolder): String {
        return ""
    }

    fun getHtml(
        siteConfig: SiteConfigHolder,
        page: Page, language: String
    ): String
}
