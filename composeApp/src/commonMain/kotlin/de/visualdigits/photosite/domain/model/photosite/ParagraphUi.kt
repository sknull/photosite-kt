package de.visualdigits.photosite.domain.model.photosite

import kotlinx.serialization.Serializable

@Serializable
data class ParagraphUi(
    val image: ImageUi? = null,
    val googleMaps: GoogleMapsUi? = null,
    val texts: List<TextUi>? = null
)
