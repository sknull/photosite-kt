package de.visualdigits.photosite.service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledIf
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.File
import java.util.Locale


@SpringBootTest(properties = [
    "spring.config.import=optional:file:C:/Users/sknull/.photosite/secrets/secrets.yml"
])
@DisabledIf("de.visualdigits.photosite.service.FixPemFilesTest#Companion.secretsMissing")
class RssServiceTest @Autowired constructor(
    private val rssService: RssService
) {

    companion object {
        @JvmStatic
        fun secretsMissing(): Boolean {
            return !File("C:\\Users\\sknull\\.photosite\\secrets\\secrets.yml").exists()
        }
    }

    @Test
    fun testRenderRss() {
        val rss = rssService.renderRssFeed(Locale.GERMAN)
        println(rss)
    }
}
