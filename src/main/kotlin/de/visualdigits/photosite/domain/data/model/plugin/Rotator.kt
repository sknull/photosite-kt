package de.visualdigits.photosite.domain.data.model.plugin

import de.visualdigits.photosite.domain.data.model.common.Language
import de.visualdigits.photosite.domain.data.model.page.Page
import de.visualdigits.photosite.domain.data.model.page.content.ContentType
import de.visualdigits.photosite.domain.data.model.page.content.ImageFile
import de.visualdigits.photosite.domain.data.model.photosite.Photosite
import de.visualdigits.photosite.domain.service.ImageService
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "photosite.plugins.rotator")
class Rotator : Plugin(
    contentType = ContentType.Rotator
) {
    override fun renderHtml(page: Page, language: Language, imageService: ImageService): String {
        val sb = StringBuilder()
        val images: List<ImageFile> = page.content.images
        val n = (images.size * Math.random()).toInt()
        if (images.size > n) {
            Photosite.getRelativeResourcePath(images[n].file)
                ?.let { image ->
                    sb
                        .append("<img src=\"/")
                        .append(image)
                        .append("\" alt=\"\"/>\n")
                }
        }
        return "\n<h1>${page.path.replace("pagetree", "Moin, moin!")}</h1>\n$sb"
    }}
