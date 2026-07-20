package de.visualdigits.photosite.service

import de.visualdigits.photosite.model.common.Language
import de.visualdigits.photosite.model.page.Page
import de.visualdigits.photosite.model.page.content.ImageFile
import de.visualdigits.photosite.model.photosite.Photosite
import de.visualdigits.photosite.model.rss.Channel
import de.visualdigits.photosite.model.rss.Item
import de.visualdigits.photosite.model.rss.Rss
import de.visualdigits.photosite.util.writeValueAsXmlString
import org.springframework.stereotype.Service
import java.io.File
import java.util.Locale

@Service
class RssService(
    photosite: Photosite,
    private val imageService: ImageService
) : AbstractXmlBaseService(photosite) {

    fun renderRssFeed(
        lang: Locale
    ): String {
        val language = Language(lang.language)
        val items = listOf<Item>()
        val pageTree = photosite.pageTree
        val lastModified = pageTree.content.lastModified
        val pages = determinePages(10)
        val mutableItems = items.toMutableList()
        pages.forEach { page ->
            processPage(page, language, mutableItems)
        }

        val fixedItems = mutableItems.map { item ->
            item.copy(link = item.link?.replace(" ", "+"))
        }
        val feed = Rss(
            channel = Channel(
                title = photosite.siteTitle,
                source = photosite.siteTitle,
                link = photosite.protocol + photosite.domain,
                description = photosite.siteSubTitle,
                language = "de",
                copyright = "Stephan Knull",
                items = fixedItems,
                lastBuildDate = lastModified
            )
        )

        return feed.writeValueAsXmlString()
    }

    private fun processPage(
        page: Page,
        lang: Language,
        items: MutableList<Item>
    ) {
        val pagePath = page.path()
        when {
            pagePath.isNotEmpty() -> {
                val images: List<ImageFile> = page.content.images
                val description = if (images.isNotEmpty()) {
                    val image: ImageFile = images[0]
                    var imageName = "teaser.jpg"
                    val imageFile = File(pagePath, imageName)
                    if (!imageFile.exists()) {
                        imageName = image.name
                    }
                    val thumbUrl =
                        photosite.protocol + photosite.domain + "/" + imageService.getThumbnail(
                            image
                        )
                    val teaser = page.content.teaser
                    var description =
                        "<img src=\"$thumbUrl\"/ alt=\"\" title=\"$imageName\"><br/>"
                    if (teaser != null) {
                        val text: String = teaser.getHtml(lang)
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
                        title = page.path,
                        creator = "Stephan Knull",
                        identifier = pagePath,
                        link = "${photosite.protocol + photosite.domain}/$pagePath?mode=rss&amp;lang=$lang",
                        pubDate = page.content.lastModified,
                        encoded = description,
                    )
                )
            }
        }
    }
}
