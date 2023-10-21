package de.visualdigits.kotlin.photosite.model.page

import de.visualdigits.kotlin.photosite.model.siteconfig.SiteConfigHolder
import org.junit.jupiter.api.Test
import java.nio.file.Paths

class PageTreeHolderTest {

    @Test
    fun testPageTree() {
        val siteConfigHolder = SiteConfigHolder()
        siteConfigHolder.rootDirectory = "file:E:/Programmierung/www/webserver/www/website"
        siteConfigHolder.site = siteConfigHolder.site
        siteConfigHolder.siteUrl = siteConfigHolder.site?.protocol + siteConfigHolder.site?.domain
        siteConfigHolder.pageDirectory = siteConfigHolder.site?.rootFolder?.let { Paths.get(it, "resources", "pagetree").toFile() }

        siteConfigHolder.reloadPageTree()

        println()
    }
}
