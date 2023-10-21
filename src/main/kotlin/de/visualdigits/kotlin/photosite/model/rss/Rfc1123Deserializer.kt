package de.visualdigits.kotlin.photosite.model.rss

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer
import java.io.IOException
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import java.util.function.BiFunction
import java.util.function.Function

class Rfc1123Deserializer : InstantDeserializer<OffsetDateTime>(
    OffsetDateTime::class.java,
    DateTimeFormatter.ISO_OFFSET_DATE_TIME,
    Function { temporal: TemporalAccessor? -> OffsetDateTime.from(temporal) },
    Function { a: FromIntegerArguments -> OffsetDateTime.ofInstant(Instant.ofEpochMilli(a.value), a.zoneId) },
    Function { a: FromDecimalArguments -> OffsetDateTime.ofInstant(Instant.ofEpochSecond(a.integer, a.fraction.toLong()), a.zoneId) },
    BiFunction { d: OffsetDateTime, z: ZoneId -> if (d.isEqual(OffsetDateTime.MIN) || d.isEqual(OffsetDateTime.MAX)) d else d.withOffsetSameInstant(z.rules.getOffset(d.toLocalDateTime())) },
    true
) {

    private val formatter = DateTimeFormatter.RFC_1123_DATE_TIME

    override fun deserialize(jsonParser: JsonParser, context: DeserializationContext): OffsetDateTime? {
        return try {
            super.deserialize(jsonParser, context)
        } catch (e: IOException) {
            jsonParser.text?.let {
                OffsetDateTime.parse(it, formatter)
            }
        }
    }
}
