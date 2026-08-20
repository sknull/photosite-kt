package de.visualdigits.photosite.data.photosite.model.plugins

import kotlinx.serialization.Serializable

@Serializable
data class PluginsDescriptor(
    val enlite: EnliteDescriptor = EnliteDescriptor(),
    val html: HtmlContentDescriptor = HtmlContentDescriptor(),
    val lightbox: LightBoxDescriptor = LightBoxDescriptor(),
    val lightgallery: LightGalleryDescriptor = LightGalleryDescriptor(),
    val markdown: MarkdownContentDescriptor = MarkdownContentDescriptor(),
    val photostory: PhotoStoryDescriptor = PhotoStoryDescriptor(),
    val rotator: RotatorDescriptor = RotatorDescriptor(),
)
