package de.visualdigits.kotlin.photosite.model.page

import de.visualdigits.kotlin.photosite.model.siteconfig.SiteConfigHolder
import org.junit.jupiter.api.Test
import java.nio.file.Paths

class PageTreeHolderTest {

    @Test
    fun testPageTree() {
        val pageTreeHolder = PageTreeHolder()
        val siteConfigHolder = SiteConfigHolder()
        siteConfigHolder.rootDirectory = "file:E:/Programmierung/www/webserver/www/website"
        pageTreeHolder.siteConfig = siteConfigHolder
        pageTreeHolder.pageFactory = PageFactory()
        pageTreeHolder.site = siteConfigHolder.getSite()
        pageTreeHolder.siteUrl = pageTreeHolder.site?.protocol + pageTreeHolder.site?.domain
        pageTreeHolder.pageDirectory = pageTreeHolder.site?.rootFolder?.let { Paths.get(it, "resources", "pagetree").toFile() }

        pageTreeHolder.reloadPageTree()

        println()
    }
}
