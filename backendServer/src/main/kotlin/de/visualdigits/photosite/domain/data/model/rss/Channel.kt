package de.visualdigits.photosite.domain.data.model.rss

import de.visualdigits.photosite.domain.data.serializer.OffsetDateTimeDeserializer
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import java.time.OffsetDateTime

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
    @Serializable(with = OffsetDateTimeDeserializer::class) @XmlElement(true) @XmlSerialName(prefix = "dc", namespace = "http://purl.org/dc/elements/1.1/", value = "date") val date: OffsetDateTime = OffsetDateTime.now(),
    @XmlElement(true) val updatePeriod: String? = null,
    @XmlElement(true) val updateFrequency: String? = null,
    @Serializable(with = OffsetDateTimeDeserializer::class) @XmlSerialName("updateBase") @XmlElement(true) val updateBase: OffsetDateTime = OffsetDateTime.now(),
    @XmlElement(true) val broadcasting: String? = null,
    @XmlElement(true) @XmlSerialName("image") val image: Image? = null,
    @XmlElement(true) val copyright: String? = null,
    @Serializable(with = OffsetDateTimeDeserializer::class) @XmlSerialName("lastBuildDate") @XmlElement(true) val lastBuildDate: OffsetDateTime = OffsetDateTime.now(),
    @Serializable(with = OffsetDateTimeDeserializer::class) @XmlSerialName("pubDate") @XmlElement(true) val pubDate: OffsetDateTime = OffsetDateTime.now(),
    @XmlElement(true) val docs: String? = null,
    @XmlElement(true) val ttl: Int? = null,
    @XmlElement(true) val itemRefs: List<String> = listOf(),
    @XmlElement(true) @XmlSerialName("item") val items: List<Item>? = null
)
