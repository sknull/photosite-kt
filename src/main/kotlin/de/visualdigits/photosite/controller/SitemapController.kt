package de.visualdigits.photosite.controller

import de.visualdigits.photosite.service.SitemapService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class SitemapController(
    private val sitemapService: SitemapService
) {

    @GetMapping(value = ["/sitemap-index.xml"])
    @ResponseBody
    fun sitemapIndex(response: HttpServletResponse) {
        sitemapService.renderSitemapIndex(response)
    }

    @GetMapping(value = ["/sitemap-site.xml"])
    @ResponseBody
    fun sitemapSite(response: HttpServletResponse) {
        sitemapService.renderSitemapSite(response)
    }

    @GetMapping(value = ["/sitemap-page.xml"])
    @ResponseBody
    fun sitemapPage(response: HttpServletResponse) {
        sitemapService.renderSitemapPage(response)
    }

    @GetMapping(value = ["/sitemap-post.xml"])
    @ResponseBody
    fun sitemapPost(response: HttpServletResponse) {
        sitemapService.renderSitemapPost(response)
    }

    @GetMapping(value = ["/sitemap.xsl"], produces = ["text/xsl"])
    fun sitemapXsl(@RequestParam(name = "page") page: String, model: Model): String {
        sitemapService.renderSitemapXsl(page, model)
        return "/xsltemplate"
    }
}

