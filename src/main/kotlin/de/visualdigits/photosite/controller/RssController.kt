package de.visualdigits.photosite.controller

import de.visualdigits.photosite.service.RssService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.util.Locale

@Controller
class RssController(
    private val rssService: RssService
) {

    @GetMapping(value = ["/rss.xml"], produces = ["application/xml"])
    @ResponseBody
    fun rssFeed(
        @RequestParam(name = "lang", required = false, defaultValue = "de") lang: Locale
    ): String {
        return rssService.renderRssFeed(lang)
    }
}

