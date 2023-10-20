package de.visualdigits.kotlin.photosite.model.page

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import de.visualdigits.kotlin.photosite.model.common.Image
import de.visualdigits.kotlin.photosite.model.page.teaser.GoogleMaps
import de.visualdigits.kotlin.photosite.model.page.teaser.Text

class Paragraph(
    var image: Image? = null,

    @JacksonXmlProperty(localName = "googlemaps")
    val googleMaps: GoogleMaps? = null,

    val texts: List<Text>? = null
)
