package de.visualdigits.photosite.model.siteconfig.plugin

class Plugins(
    val rotator: Rotator = Rotator(),
    val lightbox: LightBox = LightBox(),
    val lightgallery: LightGallery = LightGallery(),
    val photostory: PhotoStory = PhotoStory(),
    val enlite: Enlite = Enlite(),
) {
    fun plugins(): List<Plugin> = listOf(
        rotator,
        lightbox,
        lightgallery,
        photostory,
        enlite
    )
}
