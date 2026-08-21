package de.visualdigits.photosite.presentation.model

import kotlinx.serialization.Serializable

@Serializable
data class GoogleMapsDto(
    val name: String? = null,
    val width: String? = null,
    val height: String? = null,
    val align: String? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    val zoom: Int? = null,
)
