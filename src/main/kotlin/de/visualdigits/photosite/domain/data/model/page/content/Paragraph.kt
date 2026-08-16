package de.visualdigits.photosite.domain.data.model.page.content

import kotlinx.serialization.Serializable

@Serializable
data class Paragraph(
    var image: Image? = null,
    val googleMaps: GoogleMaps? = null,
    val texts: List<Text>? = null
)
