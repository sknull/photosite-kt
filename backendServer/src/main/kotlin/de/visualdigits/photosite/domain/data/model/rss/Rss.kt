package de.visualdigits.photosite.domain.data.model.rss

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.ExperimentalXmlUtilApi
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlNamespaceDeclSpec
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@OptIn(ExperimentalXmlUtilApi::class)
@Serializable
@XmlSerialName("rss")
@XmlNamespaceDeclSpec("content=http://purl.org/rss/1.0/modules/content/;dc=http://purl.org/dc/elements/1.1/")
data class Rss(
    val version: String? = null,
    @XmlSerialName("channel") val channel: Channel? = null,
    @XmlElement(true) val about: String? = null,
    @XmlSerialName("item")val items: List<Item>? = null
)
