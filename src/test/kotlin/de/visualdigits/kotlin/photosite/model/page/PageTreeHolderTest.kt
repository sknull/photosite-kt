package de.visualdigits.kotlin.photosite.model.page

import de.visualdigits.kotlin.photosite.model.siteconfig.SiteConfigHolder
import org.junit.jupiter.api.Test
import java.nio.file.Paths

class PageTreeHolderTest {

    @Test
    fun testPageTree() {
        val siteConfigHolder = SiteConfigHolder()
        siteConfigHolder.rootDirectory = "file:E:/Programmierung/www/webserver/www/website"
        siteConfigHolder.siteUrl = siteConfigHolder.siteConfig?.site?.protocol + siteConfigHolder.siteConfig?.site?.domain
        siteConfigHolder.pageDirectory = siteConfigHolder.siteConfig?.site?.rootFolder?.let { Paths.get(it, "resources", "pagetree").toFile() }

        siteConfigHolder.reloadPageTree()

        println()
    }
}
