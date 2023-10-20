package de.visualdigits.kotlin.photosite.model.siteconfig

import org.junit.jupiter.api.Test
import java.io.File

class SiteConfigTest {

    @Test
    fun testLoadConfig() {
        val config = SiteConfig.load(File("E:/Programmierung/www/webserver/www/website/resources/config.xml"))
        println(config)
    }
}
