package de.visualdigits.photosite.service

import de.visualdigits.photosite.domain.service.RssService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.Locale


@SpringBootTest(properties = [
    "spring.config.import=optional:file:C:/Users/sknull/.photosite/secrets/secrets.yml"
])
@DisabledIfEnvironmentVariable(named = "CI", matches = "true")
class RssServiceTest @Autowired constructor(
    private val rssService: RssService
) {

    @Test
    fun testRenderRss() {
        val rss = rssService.renderRssFeed(Locale.GERMAN)
        println(rss)
    }
}
