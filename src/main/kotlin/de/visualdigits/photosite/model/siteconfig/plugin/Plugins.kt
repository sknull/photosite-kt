package de.visualdigits.photosite.model.siteconfig.plugin

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
