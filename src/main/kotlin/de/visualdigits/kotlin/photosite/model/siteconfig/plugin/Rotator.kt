package de.visualdigits.kotlin.photosite.model.siteconfig.plugin

import de.visualdigits.kotlin.photosite.model.common.ImageFile
import de.visualdigits.kotlin.photosite.model.page.Page
import de.visualdigits.kotlin.photosite.model.siteconfig.SiteConfig

class Rotator : Plugin(
    name = "Rotator",
    clazz = "de.visualdigits.kotlin.photosite.model.siteconfig.plugin.Rotator"
) {
    override fun getHtml(siteConfig: SiteConfig, page: Page, language: String): String {
        val sb = StringBuilder()
        val images: List<ImageFile> = page.images
        val n = (images.size * Math.random()).toInt()
        if (images.size > n) {
            siteConfig
                .getRelativeResourcePath(images[n].file)
                ?.let {
                    sb
                        .append("<img src=\"/")
                        .append(it)
                        .append("\"/>\n")
                }
        }
        return sb.toString()
    }}
