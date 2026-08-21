package de.visualdigits.photosite.data.filesystem.model

import kotlinx.serialization.Serializable

@Serializable
data class TeaserDescriptor(
    val googleMaps: GoogleMapsDescriptor? = null,
    val texts: List<TextDescriptor> = listOf()
)
