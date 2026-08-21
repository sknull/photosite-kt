package de.visualdigits.photosite.domain.data.model.rss


import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlValue

@Serializable
data class Text(
    @XmlElement(false) val type: String? = null,
    @XmlValue val text: String
)
