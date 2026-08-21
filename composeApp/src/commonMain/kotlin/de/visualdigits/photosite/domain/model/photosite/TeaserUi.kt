package de.visualdigits.photosite.domain.model.photosite

import kotlinx.serialization.Serializable

@Serializable
data class TeaserUi(
    val googleMaps: GoogleMapsUi? = null,
    val texts: List<TextUi> = listOf()
)
