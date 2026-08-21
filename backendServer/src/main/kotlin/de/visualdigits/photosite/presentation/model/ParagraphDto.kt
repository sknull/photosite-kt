package de.visualdigits.photosite.presentation.model

import kotlinx.serialization.Serializable

@Serializable
data class ParagraphDto(
    val image: ImageDto? = null,
    val googleMaps: GoogleMapsDto? = null,
    val texts: List<TextDto>? = null
)
