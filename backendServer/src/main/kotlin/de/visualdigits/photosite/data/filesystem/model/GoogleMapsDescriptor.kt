package de.visualdigits.photosite.data.filesystem.model

import kotlinx.serialization.Serializable

@Serializable
class GoogleMapsDescriptor(
    val name: String? = null,
    val width: String? = null,
    val height: String? = null,
    val align: String? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    val zoom: Int? = null,
)
