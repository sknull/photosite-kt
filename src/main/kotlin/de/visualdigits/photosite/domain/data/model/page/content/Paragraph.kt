package de.visualdigits.photosite.domain.data.model.page.content

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.UUID

@Serializable
data class Paragraph(
    @Transient val id: UUID? = null,
    var image: Image? = null,
    val googleMaps: GoogleMaps? = null,
    val texts: List<Text>? = null
)
