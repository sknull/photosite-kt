package de.visualdigits.kotlin.photosite.model.page

import de.visualdigits.kotlin.photosite.model.siteconfig.SiteConfig
import de.visualdigits.kotlin.photosite.model.siteconfig.SiteConfigHolder
import org.junit.jupiter.api.Test
import java.nio.file.Paths

class PageTreeHolderTest {

    @Test
    fun testPageTree() {
        val siteConfigHolder = SiteConfigHolder()
        val rootFolder = "E:/Programmierung/www/webserver/www/website"
        siteConfigHolder.rootDirectory = "file:$rootFolder"
        siteConfigHolder.siteConfig = SiteConfig.load(Paths.get(rootFolder, "resources", "config.xml").toFile())
        siteConfigHolder.siteConfig?.site?.rootFolder = rootFolder
        siteConfigHolder.siteUrl = siteConfigHolder.siteConfig?.site?.protocol + siteConfigHolder.siteConfig?.site?.domain
        siteConfigHolder.pageDirectory = siteConfigHolder.siteConfig?.site?.rootFolder?.let { Paths.get(it, "resources", "pagetree").toFile() }
        siteConfigHolder.reloadPageTree()

        val pages = siteConfigHolder.pageTree?.getSubTree("pagetree/Arts/Manikin Records")
        println(pages)
    }
}
