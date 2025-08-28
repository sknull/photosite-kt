package de.visualdigits.photosite.model.page

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import de.visualdigits.photosite.model.common.Image
import de.visualdigits.photosite.model.page.teaser.GoogleMaps
import de.visualdigits.photosite.model.page.teaser.Text

class Paragraph(
    var image: Image? = null,

    @JacksonXmlProperty(localName = "googlemaps")
    val googleMaps: GoogleMaps? = null,

    val texts: List<Text>? = null
)
