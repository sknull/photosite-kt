package de.visualdigits.photosite.domain.data.model.photosite

import de.visualdigits.photosite.domain.data.model.plugin.Enlite
import de.visualdigits.photosite.domain.data.model.plugin.HtmlContent
import de.visualdigits.photosite.domain.data.model.plugin.LightBox
import de.visualdigits.photosite.domain.data.model.plugin.LightGallery
import de.visualdigits.photosite.domain.data.model.plugin.MarkdownContent
import de.visualdigits.photosite.domain.data.model.plugin.PhotoStory
import de.visualdigits.photosite.domain.data.model.plugin.Plugin
import de.visualdigits.photosite.domain.data.model.plugin.Rotator

class Plugins(
    val enlite: Enlite = Enlite(),
    val html: HtmlContent = HtmlContent(),
    val lightbox: LightBox = LightBox(),
    val lightgallery: LightGallery = LightGallery(),
    val markdown: MarkdownContent = MarkdownContent(),
    val photostory: PhotoStory = PhotoStory(),
    val rotator: Rotator = Rotator(),
) {
    fun plugins(): List<Plugin> = listOf(
        enlite,
        html,
        lightbox,
        lightgallery,
        markdown,
        rotator,
        photostory
    )
}
