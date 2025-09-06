package de.visualdigits.photosite.service

import de.visualdigits.photosite.model.photosite.Photosite
import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.io.IOUtils
import org.apache.commons.text.StringEscapeUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.ui.Model
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Paths
import java.time.OffsetDateTime

@Service
class SitemapService(
    photosite: Photosite
) : AbstractXmlBaseService(photosite) {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        protected val MIMETYPE_XML = "application/xml"
    }

    fun renderSitemapIndex(response: HttpServletResponse) {
        var body = "  <sitemap>\n"
        body += "    <loc>${photosite.protocol + photosite.domain}/sitemap-site.xml</loc>\n"
        body += "    <lastmod>${isoDate(photosite.pageTree.lastModified)}</lastmod>\n"
        body += "  </sitemap>\n"
        body += "  <sitemap>\n"
        body += "    <loc>${photosite.protocol + photosite.domain}/sitemap-page.xml</loc>\n"
        body += "    <lastmod>${isoDate(photosite.pageTree.lastModified)}</lastmod>\n"
        body += "  </sitemap>\n"
        body += "  <sitemap>\n"
        body += "    <loc>${photosite.protocol + photosite.domain}/sitemap-post.xml</loc>\n"
        body += "    <lastmod>${isoDate(photosite.pageTree.lastModified)}</lastmod>\n"
        body += "  </sitemap>\n"
        sendSitemap(
            response,
            body,
            "index",
            "sitemapindex",
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/siteindex.xsd\" xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9"
        )
    }
    fun renderSitemapSite(response: HttpServletResponse) {
        var body = "  <url>\n"
        body += "    <loc>${photosite.protocol + photosite.domain}</loc>\n"
        body += "    <lastmod>${isoDate(photosite.pageTree.lastModified)}</lastmod>\n"
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

    fun renderSitemapPage(response: HttpServletResponse) {
        log.info("Rendering page site map...")
        val sb = StringBuilder()
        determinePages { p -> p.children.isNotEmpty() && p.lastModified > OffsetDateTime.MIN }.forEach { page ->
            sb
                .append("  <url>\n")
                .append("    <loc>")
                .append(photosite.protocol + photosite.domain)
                .append("/")
                .append(StringEscapeUtils.escapeXml11(page.path()))
                .append("</loc>\n")
                .append("    <lastmod>")
                .append(isoDate(page.lastModified))
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

    fun renderSitemapPost(response: HttpServletResponse) {
        log.info("Rendering post site map...")
        val sb = StringBuilder()
        determinePages { p -> p.children.isEmpty() && p.content.images.isNotEmpty() && p.lastModified > OffsetDateTime.MIN }.forEach { page ->
            sb
                .append("  <url>\n").append("    <loc>")
                .append(photosite.protocol + photosite.domain).append("/")
                .append(StringEscapeUtils.escapeXml11(page.path()))
                .append("</loc>\n")
                .append("    <lastmod>")
                .append(isoDate(page.lastModified))
                .append("</lastmod>\n")
            for (imageFile in page.content.images) {
                val imagePath = Photosite.getRelativeResourcePath(imageFile.file)
                sb
                    .append("    <image:image>\n")
                    .append("      <image:loc>")
                    .append(photosite.protocol + photosite.domain).append("/")
                    .append(StringEscapeUtils.escapeXml11(imagePath))
                    .append("</image:loc>\n")
                val caption = page.content.captionsMap[imageFile.name]
                caption?.also { c ->
                    c.translationsMap[photosite.languageDefault]?.let { l ->
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

    fun renderSitemapXsl(page: String, model: Model) {
        model.addAttribute("theme", photosite.theme)
        model.addAttribute("siteUrl", photosite.protocol + photosite.domain)
        model.addAttribute("language", photosite.languageDefault)
        model.addAttribute("title", photosite.siteTitle)
        model.addAttribute("breadcrumb", page)
        var comments = "<!--\n"
        comments += "     PAGE  : sitemap-$page\n"
        comments += "-->"
        model.addAttribute("comments", comments)
        val xslFile = Paths.get(Photosite.rootDirectory.canonicalPath, "resources", "themes", photosite.theme, "sitemap", "sitemap-$page.xsl").toFile()
        val content = readFile(xslFile)
        model.addAttribute("content", content)
    }

    private fun readFile(xslFile: File): String? {
        var content: String? = null
        try {
            FileInputStream(xslFile).use { ins ->
                ByteArrayOutputStream().use { baos ->
                    IOUtils.copy(ins, baos)
                    content = baos.toString()
                }
            }
        } catch (e: IOException) {
            log.error("Could not read xsl file: $xslFile", e)
        }
        return content
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

    private fun sendContent(
        content: String,
        mimeType: String,
        headers: Map<String, String> = mapOf(),
        response: HttpServletResponse
    ) {
        try {
            ByteArrayInputStream(content.toByteArray(StandardCharsets.UTF_8)).use { ins ->
                response.outputStream.use { outs ->
                    response.contentType = mimeType
                    headers.forEach(response::addHeader)
                    IOUtils.copy(ins, outs)
                }
            }
        } catch (e: IOException) {
            throw IllegalStateException("Could not hand out rss stream", e)
        }
    }
}
