package de.visualdigits.kotlin.photosite.controller

import de.visualdigits.kotlin.photosite.model.siteconfig.navi.PageTree
import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.text.StringEscapeUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.nio.file.Paths

@Controller
class SitemapController : AbstractXmlBaseController() {

    private val log = LoggerFactory.getLogger(SitemapController::class.java)
    
    @GetMapping(value = ["/sitemap-index.xml"])
    @ResponseBody
    fun sitemapIndex(response: HttpServletResponse) {
        val site = siteConfigHolder.siteConfig?.site
        val pageTree =
            PageTree(Paths.get(site?.rootFolder!!, site.resourcesRoot, "pagetree").toFile())
        val lastModified = isoDate(pageTree.lastModified())
        val siteUrl: String = site.protocol + site.domain
        var body = "  <sitemap>\n"
        body += "    <loc>$siteUrl/sitemap-site.xml</loc>\n"
        body += "    <lastmod>$lastModified</lastmod>\n"
        body += "  </sitemap>\n"
        body += "  <sitemap>\n"
        body += "    <loc>$siteUrl/sitemap-page.xml</loc>\n"
        body += "    <lastmod>$lastModified</lastmod>\n"
        body += "  </sitemap>\n"
        body += "  <sitemap>\n"
        body += "    <loc>$siteUrl/sitemap-post.xml</loc>\n"
        body += "    <lastmod>$lastModified</lastmod>\n"
        body += "  </sitemap>\n"
        sendSitemap(
            response,
            body,
            "index",
            "sitemapindex",
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/siteindex.xsd\" xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9"
        )
    }

    @GetMapping(value = ["/sitemap-site.xml"])
    @ResponseBody
    fun sitemapSite(response: HttpServletResponse) {
        val site = siteConfigHolder.siteConfig?.site
        val pageTree = PageTree(Paths.get(site?.rootFolder!!, "resources", "pagetree").toFile())
        val lastModified = isoDate(pageTree.lastModified())
        val siteUrl: String = site.protocol + site.domain
        var body = "  <url>\n"
        body += "    <loc>$siteUrl</loc>\n"
        body += "    <lastmod>$lastModified</lastmod>\n"
        //        body += "    <changefreq>" + self.changefreq + "</changefreq>\n"
//        body += "    <priority>" + self.priority + "</priority>\n"
        body += "  </url>\n"
        sendSitemap(
            response,
            body,
            "site",
            "urlset",
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/siteindex.xsd\" xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9"
        )
    }

    @GetMapping(value = ["/sitemap-page.xml"])
    @ResponseBody
    fun sitemapPage(response: HttpServletResponse) {
        log.info("Rendering page site map...")
        val site = siteConfigHolder.siteConfig?.site
        val siteUrl: String = site?.protocol + site?.domain
        val sb = StringBuilder()
        determinePages().forEach { page ->
            sb
                .append("  <url>\n")
                .append("    <loc>")
                .append(siteUrl)
                .append("/")
                .append(StringEscapeUtils.escapeXml11(page.normalizedPath()))
                .append("</loc>\n")
                .append("    <lastmod>")
                .append(isoDate(page.lastModifiedTimestamp))
                .append("</lastmod>\n")
                //                        .append("    <changefreq>" + self.changefreq + "</changefreq>\n")
                //                        .append("    <priority>" + self.priority + "</priority>\n")
                .append("  </url>\n")
        }
        sendSitemap(
            response,
            sb.toString(),
            "page",
            "urlset",
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd\" xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" xmlns:image=\"http://www.google.com/schemas/sitemap-image/1.1"
        )
    }

    @GetMapping(value = ["/sitemap-post.xml"])
    @ResponseBody
    fun sitemapPost(response: HttpServletResponse) {
        log.info("Rendering post site map...")
        val site = siteConfigHolder.siteConfig?.site
        val siteUrl = site?.protocol + site?.domain
        val lang = site?.languageDefault!!
        val sb = StringBuilder()
        determinePages().forEach { page ->
            sb
                .append("  <url>\n").append("    <loc>")
                .append(siteUrl).append("/")
                .append(StringEscapeUtils.escapeXml11(page.normalizedPath()))
                .append("</loc>\n")
                .append("    <lastmod>")
                .append(isoDate(page.lastModifiedTimestamp))
                .append("</lastmod>\n")
            for (imageFile in page.images) {
                val imagePath = siteConfigHolder.siteConfig?.getRelativeResourcePath(imageFile.file)
                sb
                    .append("    <image:image>\n")
                    .append("      <image:loc>")
                    .append(siteUrl).append("/")
                    .append(StringEscapeUtils.escapeXml11(imagePath))
                    .append("</image:loc>\n")
                val caption = page.content?.captionsMap?.get(imageFile.name)
                caption?.let { c ->
                    c.getI18n(lang)?.let { l ->
                        l.alt?:l.title?.let { captionText ->
                            sb
                                .append("      <image:title><![CDATA[")
                                .append(StringEscapeUtils.escapeXml11(captionText))
                                .append("]]></image:title>\n")
                        }
                    }
                }
                sb.append("    </image:image>\n")
            }
            sb.append("  </url>\n")
        }
        sendSitemap(
            response,
            sb.toString(),
            "post",
            "urlset",
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd\" xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" xmlns:image=\"http://www.google.com/schemas/sitemap-image/1.1"
        )
    }

    @GetMapping(value = ["/sitemap.xsl"], produces = ["text/xsl"])
    fun sitemapXsl(@RequestParam(name = "page") page: String, model: Model): String {
        val site = siteConfigHolder.siteConfig?.site
        val language = site?.languageDefault
        val theme = site?.theme!!
        val siteUrl = site.protocol + site.domain
        model.addAttribute("theme", theme)
        model.addAttribute("siteUrl", siteUrl)
        model.addAttribute("language", language)
        model.addAttribute("title", site.siteTitle)
        model.addAttribute("breadcrumb", page)
        var comments = "<!--\n"
        comments += "     PAGE  : sitemap-$page\n"
        comments += "-->"
        model.addAttribute("comments", comments)
        val xslFile = Paths.get(
            site.rootFolder!!, site.resourcesRoot, "theme", site.theme, "sitemap",
            "sitemap-$page.xsl"
        ).toFile()
        val content = readFile(xslFile)
        model.addAttribute("content", content)
        return "/xsltemplate"
    }

    private fun sendSitemap(
        response: HttpServletResponse,
        body: String,
        page: String,
        rootTag: String,
        schemas: String
    ) {
        var xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        xml += "<?xml-stylesheet type=\"text/xsl\" href=\"/sitemap.xsl?page=$page\"?>\n"
        xml += "<$rootTag$schemas\">\n"
        xml += body
        xml += "</$rootTag>\n"
        sendContent(content = xml, mimeType = MIMETYPE_XML, response = response)
    }
}

