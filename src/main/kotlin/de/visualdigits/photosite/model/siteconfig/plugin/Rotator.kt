package de.visualdigits.photosite.model.siteconfig.plugin

import de.visualdigits.photosite.model.common.ImageFile
import de.visualdigits.photosite.model.page.Page
import de.visualdigits.photosite.model.pagemodern.ContentType
import de.visualdigits.photosite.model.siteconfig.Photosite
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "photosite.plugins.rotator")
class Rotator : Plugin(
    contentType = ContentType.Rotator
) {
    override fun getHtml(page: Page, language: String): String {
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
