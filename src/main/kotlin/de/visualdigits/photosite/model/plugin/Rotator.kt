package de.visualdigits.photosite.model.plugin

import de.visualdigits.photosite.model.page.Page
import de.visualdigits.photosite.model.page.content.ContentType
import de.visualdigits.photosite.model.page.content.ImageFile
import de.visualdigits.photosite.model.photosite.Photosite
import de.visualdigits.photosite.service.ImageService
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.util.Locale

@Component
@ConfigurationProperties(prefix = "photosite.plugins.rotator")
class Rotator : Plugin(
    contentType = ContentType.Rotator
) {
    override fun renderHtml(page: Page, language: Locale, imageService: ImageService): String {
        val sb = StringBuilder()
        val images: List<ImageFile> = page.content.images
        val n = (images.size * Math.random()).toInt()
        if (images.size > n) {
            Photosite.getRelativeResourcePath(images[n].file)
                ?.let {
                    sb
                        .append("<img src=\"/")
                        .append(it)
                        .append("\"/>\n")
                }
        }
        return sb.toString()
    }}
