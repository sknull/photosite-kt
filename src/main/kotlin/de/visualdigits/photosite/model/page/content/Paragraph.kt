package de.visualdigits.photosite.model.page.content

import kotlinx.serialization.Serializable

@Serializable
class Paragraph(
    var image: Image? = null,
    val googleMaps: GoogleMaps? = null,
    val texts: List<Text>? = null
)
