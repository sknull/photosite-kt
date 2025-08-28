package de.visualdigits.photosite.model.rss

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.kotlinModule
import java.io.File
import java.io.IOException
import java.time.OffsetDateTime

@JacksonXmlRootElement(localName = "rss")
data class Rss(
    @JacksonXmlProperty(isAttribute = true) val version: String? = "2.0",
    @JacksonXmlProperty(isAttribute = true, localName = "xmlns:content") val xmlnsContent: String = "http://purl.org/rss/1.0/modules/content/",
    @JacksonXmlProperty(isAttribute = true, localName = "xmlns:dc") val xmlnsDc: String = "http://purl.org/dc/elements/1.1/",
    @JacksonXmlProperty(localName = "channel") @JacksonXmlElementWrapper(useWrapping = false, localName = "channel") val channels: List<Channel>? = null,
) {
    companion object {
        val xmlMapper = XmlMapper
            .builder()
            .addModule(kotlinModule())
            .addModule(JavaTimeModule().addSerializer(DateTimeWithoutMillisSerializer()))
            .addModule(SimpleModule().addDeserializer(OffsetDateTime::class.java, Rfc1123Deserializer()))
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) // ISODate
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .disable(ToXmlGenerator.Feature.WRITE_XML_DECLARATION)
            .serializationInclusion(JsonInclude.Include.NON_EMPTY)
            .defaultUseWrapper(false)
            .build()
    }

    fun unmarshall(rssFile: File): Rss? {
        return runCatching {
            xmlMapper.readValue(
                rssFile,
                Rss::class.java
            )
        }.onFailure { e ->
            throw IllegalStateException("Could not parse rss file: $rssFile", e)
        }.getOrThrow()
    }

    fun marshall(rssFile: File) {
        try {
            xmlMapper.writeValue(rssFile, this)
        } catch (e: IOException) {
            throw IllegalStateException("Could not marshall to file: $rssFile", e)
        }
    }

    fun marshall(): String {
        return try {
            xmlMapper.writeValueAsString(this)
        } catch (e: IOException) {
            throw IllegalStateException("Could not marshall to string", e)
        }
    }
}
