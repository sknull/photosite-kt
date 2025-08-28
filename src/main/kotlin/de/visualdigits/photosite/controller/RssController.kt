package de.visualdigits.photosite.controller

import de.visualdigits.photosite.model.common.ImageFile
import de.visualdigits.photosite.model.page.Page
import de.visualdigits.photosite.model.rss.Channel
import de.visualdigits.photosite.model.rss.Item
import de.visualdigits.photosite.model.rss.Rss
import de.visualdigits.photosite.model.siteconfig.Photosite
import de.visualdigits.photosite.util.ImageHelper
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.io.File

@Controller
class RssController(
    photosite: Photosite
) : AbstractXmlBaseController(photosite) {

    @GetMapping(value = ["/rss.xml"], produces = ["application/xml"])
    @ResponseBody
    fun rssFeed(
        @RequestParam(name = "lang", required = false, defaultValue = "") lang: String, response: HttpServletResponse
    ): String {
        val items = mutableListOf<Item>()
        val pageTree = photosite.pageTree
        val lastModified = pageTree.lastModified()
        val feed = Rss(
            channels = listOf(
                Channel(
                    title = photosite.siteTitle,
                    generator = photosite.siteTitle,
                    link = photosite.protocol + photosite.domain,
                    description = photosite.siteSubTitle,
                    language = "de",
                    copyright = "Stephan Knull",
                    items = items,
                    lastBuildDate = fullDate(lastModified)
                )
            )
        )
        val pages = determinePages(10)
        pages.forEach { page ->
            processPage(page, lang, items)
        }

        return feed.marshall()
    }

    private fun processPage(
        page: Page,
        lang: String,
        items: MutableList<Item>
    ) {
        val pagePath = page.normalizedPath()
        when {
            pagePath.isNotEmpty() -> {
                val images: List<ImageFile> = page.images
                val description = if (images.isNotEmpty()) {
                    val image: ImageFile = images[0]
                    var imageName = "teaser.jpg"
                    val imageFile = File(pagePath, imageName)
                    if (!imageFile.exists()) {
                        imageName = image.name
                    }
                    val thumbUrl =
                        photosite.protocol + photosite.domain + "/" + ImageHelper.getThumbnail(
                            photosite,
                            image
                        )
                    val teaser = page.content?.teaser
                    var description =
                        "<img src=\"$thumbUrl\"/ alt=\"$imageName\" title=\"$imageName\"><br/>"
                    if (teaser != null) {
                        val text: String = teaser.getHtml(photosite, page, lang)
                        if (text.isNotBlank()) {
                            description += text.trim { it <= ' ' } + "<br/>\n"
                        }
                    }
                    description
                } else {
                    null
                }

                items.add(
                    Item(
                        title = page.name,
                        author = "Stephan Knull",
                        category = pagePath,
                        link = "${photosite.protocol + photosite.domain}/$pagePath?mode=rss&amp;lang=$lang",
                        pubDate = page.lastModifiedTimestamp,
                        description = description
                    )
                )
            }
        }
    }
}

