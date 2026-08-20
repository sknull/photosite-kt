package de.visualdigits.photosite.data.filesystem.model

import kotlinx.serialization.Serializable

@Serializable
data class ParagraphDescriptor(
    var image: ImageDescriptor? = null,
    val googleMaps: GoogleMapsDescriptor? = null,
    val texts: List<TextDescriptor> = listOf()
)
