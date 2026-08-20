package de.visualdigits.photosite.presentation.model

import kotlinx.serialization.Serializable

@Serializable
data class TeaserDto(
    val googleMaps: GoogleMapsDto? = null,
    val texts: List<TextDto> = listOf()
)
