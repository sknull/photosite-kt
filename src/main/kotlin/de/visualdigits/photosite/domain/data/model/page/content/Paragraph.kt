package de.visualdigits.photosite.domain.data.model.page.content

import java.util.UUID

data class Paragraph(
    val id: UUID? = null,
    var image: Image? = null,
    val googleMaps: GoogleMaps? = null,
    val texts: List<Text>? = null
)
