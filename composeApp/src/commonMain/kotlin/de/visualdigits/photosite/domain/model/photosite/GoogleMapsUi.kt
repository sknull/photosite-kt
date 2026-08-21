package de.visualdigits.photosite.domain.model.photosite

import kotlinx.serialization.Serializable

@Serializable
data class GoogleMapsUi(
    val name: String? = null,
    val width: String? = null,
    val height: String? = null,
    val align: String? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    val zoom: Int? = null,
)
