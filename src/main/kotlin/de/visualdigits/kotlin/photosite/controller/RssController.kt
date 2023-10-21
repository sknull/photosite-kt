package de.visualdigits.kotlin.photosite.controller

import de.visualdigits.kotlin.photosite.model.common.ImageFile
import de.visualdigits.kotlin.photosite.model.rss.Channel
import de.visualdigits.kotlin.photosite.model.rss.Item
import de.visualdigits.kotlin.photosite.model.rss.Rss
import de.visualdigits.kotlin.photosite.util.ImageHelper
import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.io.File

@Controller
class RssController : AbstractXmlBaseController() {

    @GetMapping(value = ["/rss.xml"], produces = ["application/xml"])
    @ResponseBody
    fun rssFeed(
        @RequestParam(name = "lang", required = false, defaultValue = "") lang: String, response: HttpServletResponse
    ): String {
        val site = siteConfigHolder.siteConfig?.site
        val items = mutableListOf<Item>()
        val pageTree = determinePageTree()
        val lastModified = pageTree.lastModified()
        val feed = Rss(
            channels = listOf(Channel(
                title = site?.siteTitle,
                generator = site?.siteTitle,
                link = site?.protocol + site?.domain,
                description = site?.siteSubTitle,
                language = "de",
                copyright = "Stephan Knull",
                items = items,
                lastBuildDate = fullDate(lastModified)
            ))
        )
        val pages = determinePages(pageTree, 10)
        pages.forEach { page ->
            val pagePath = page.normalizedPath()
            if (StringUtils.isNotEmpty(pagePath)) {
                val images: List<ImageFile> = page.images
                val description = if (images.isNotEmpty()) {
                    val image: ImageFile = images[0]
                    var imageName = "teaser.jpg"
                    val imageFile = File(pagePath, imageName)
                    if (!imageFile.exists()) {
                        imageName = image.name
                    }
                    val thumbUrl =
                        site?.protocol + site?.domain + "/" + ImageHelper.getThumbnail(siteConfigHolder.siteConfig!!, image)
                    val teaser = page.content?.teaser
                    var description =
                        "<img src=\"$thumbUrl\"/ alt=\"$imageName\" title=\"$imageName\"><br/>"
                    if (teaser != null) {
                        val text: String = teaser.getHtml(siteConfigHolder.siteConfig!!, page, lang)
                        if (StringUtils.isNotBlank(text)) {
                            description += text.trim { it <= ' ' } + "<br/>\n"
                        }
                    }
                    description
                } else null
                items.add(Item(
                    title = page.name,
                    author = "Stephan Knull",
                    category = pagePath,
                    link = "${site?.protocol + site?.domain}/$pagePath?mode=rss&amp;lang=$lang",
                    pubDate = page.lastModifiedTimestamp,
                    description = description
                ))
            }
        }

//        sendContent(content = feed.marshall(), mimeType = MIMETYPE_XML, response = response)
        return feed.marshall()
    }
}

