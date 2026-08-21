package de.visualdigits.photosite.domain.service

import de.visualdigits.photosite.domain.data.model.common.Language
import de.visualdigits.photosite.domain.data.model.page.Page
import de.visualdigits.photosite.domain.data.model.page.content.ImageFile
import de.visualdigits.photosite.domain.data.model.rss.Channel
import de.visualdigits.photosite.domain.data.model.rss.Item
import de.visualdigits.photosite.domain.data.model.rss.Rss
import de.visualdigits.photosite.domain.util.writeValueAsXmlString
import org.springframework.stereotype.Service
import java.io.File
import java.util.Locale

@Service
class RssService(
    photositeService: PhotositeService,
    private val imageService: ImageService
) : AbstractXmlBaseService(photositeService) {

    fun renderRssFeed(
        lang: Locale
    ): String {
        val language = Language(lang.language)
        val items = listOf<Item>()
        val pageTree = photositeService.photosite.pageTree
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
                title = photositeService.photosite.siteTitle,
                description = photositeService.photosite.siteSubTitle,
                language = "de",
                copyright = "Stephan Knull",
                publisher = "Stephan Knull",
                lastBuildDate = lastModified,
                link = photositeService.photosite.protocol + photositeService.photosite.domain,
                items = fixedItems
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
                        photositeService.photosite.protocol + photositeService.photosite.domain + "/" + imageService.getThumbnail(
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
                        link = "${photositeService.photosite.protocol + photositeService.photosite.domain}/$pagePath?mode=rss&amp;lang=$lang",
                        subject = (page.content.keywords + pagePath.split("/").distinct().sorted()).joinToString(","),
                        creator = "Stephan Knull",
                        identifier = pagePath,
                        pubDate = page.content.lastModified,
                        content = description,
                    )
                )
            }
        }
    }
}
