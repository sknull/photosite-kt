package de.visualdigits.photosite.domain.data.model.rss

import de.visualdigits.photosite.domain.data.model.common.KmpOffsetDateTime
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
data class Channel(
    @XmlElement(false) val version: String? = null,
    @XmlElement(true) val title: String? = null,
    @XmlElement(true) val link: String? = null,
    @XmlElement(true) val category: String? = null,
    @XmlElement(true) val subject: String? = null,
    @XmlElement(true) val description: String? = null,
    @XmlElement(true) @XmlSerialName(prefix = "dc", namespace = "http://purl.org/dc/elements/1.1/", value = "source") val source: String? = null,
    @XmlElement(true) @XmlSerialName(prefix = "dc", namespace = "http://purl.org/dc/elements/1.1/", value = "publisher") val publisher: String? = null,
    @XmlElement(true) @XmlSerialName(prefix = "dc", namespace = "http://purl.org/dc/elements/1.1/", value = "language") val language: String? = null,
    @XmlElement(true) @XmlSerialName(prefix = "dc", namespace = "http://purl.org/dc/elements/1.1/", value = "rights") val rights: String? = null,
    @XmlElement(true) @XmlSerialName(prefix = "dc", namespace = "http://purl.org/dc/elements/1.1/", value = "date") val date: KmpOffsetDateTime = KmpOffsetDateTime.now(),
    @XmlElement(true) val updatePeriod: String? = null,
    @XmlElement(true) val updateFrequency: String? = null,
    @XmlSerialName("updateBase") @XmlElement(true) val updateBase: KmpOffsetDateTime = KmpOffsetDateTime.now(),
    @XmlElement(true) val broadcasting: String? = null,
    @XmlElement(true) @XmlSerialName("image") val image: Image? = null,
    @XmlElement(true) val copyright: String? = null,
    @XmlSerialName("lastBuildDate") @XmlElement(true) val lastBuildDate: KmpOffsetDateTime = KmpOffsetDateTime.now(),
    @XmlSerialName("pubDate") @XmlElement(true) val pubDate: KmpOffsetDateTime = KmpOffsetDateTime.now(),
    @XmlElement(true) val docs: String? = null,
    @XmlElement(true) val ttl: Int? = null,
    @XmlElement(true) val itemRefs: List<String> = listOf(),
    @XmlElement(true) @XmlSerialName("item") val items: List<Item>? = null
)
