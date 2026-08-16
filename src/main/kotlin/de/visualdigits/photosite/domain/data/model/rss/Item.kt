package de.visualdigits.photosite.domain.data.model.rss

import de.visualdigits.photosite.domain.data.model.common.KmpOffsetDateTime
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlCData
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import org.apache.commons.text.StringEscapeUtils

@Serializable
data class Item(
    @XmlElement(true) @XmlSerialName("guid") val guid: Guid? = null,
    @XmlElement(true) val identifier: String? = null,
    @XmlElement(true) val id: String? = null,

    @XmlElement(true) @XmlSerialName("pubDate") val pubDate: KmpOffsetDateTime? = null, // update date time or first publish date time when date is empty
    @XmlElement(true) val about: String? = null,

    @XmlElement(true) val creator: String? = null,
    @XmlElement(true) val type: String? = null,
    @XmlElement(true) val format: String? = null,
    @XmlElement(true) val source: String? = null,
    @XmlElement(true) val language: String? = null,
    @XmlElement(true) val publisher: String? = null,
    @XmlElement(true) val rights: String? = null,
    @XmlElement(true) val subject: String? = null,
    @XmlElement(true) val audience: String? = null,
    @XmlElement(true) val isFormatOf: String? = null,
    @XmlElement(true) @XmlSerialName(prefix = "dc", namespace = "http://purl.org/dc/elements/1.1/", value = "date") val date: KmpOffsetDateTime? = null, // first publish date time
    @XmlCData @XmlElement(true) @XmlSerialName(prefix = "content", namespace = "http://purl.org/rss/1.0/modules/content/", value = "encoded") var content: String? = null,
    @XmlElement(true) val topline: String? = null,
    @XmlElement(true) val states: String? = null,

    @XmlElement(true) val title: String? = null,
    @XmlElement(true) val link: String? = null,
    @XmlElement(true) val description: String? = null,
    @XmlElement(true) @XmlSerialName("category") val categories: List<Text> = listOf(),
    @XmlElement(true) val isPermaLink: Boolean? = null,
    @XmlElement(true) @XmlSerialName("enclosure")val enclosure: Enclosure? = null,
    @XmlElement(true) @XmlSerialName("image")val images: List<Image> = listOf(),

    @XmlElement(true) @XmlSerialName("comment") val comments: MutableList<Comment> = mutableListOf(),
) {

    val contentUnescaped: String?
        get() {
            return content?.let { e -> StringEscapeUtils.unescapeXml(e) }
        }
}
