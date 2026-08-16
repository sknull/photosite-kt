package de.visualdigits.photosite.domain.data.model.rss

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlValue

@Serializable
data class Content(
    @XmlValue val text: String
)
