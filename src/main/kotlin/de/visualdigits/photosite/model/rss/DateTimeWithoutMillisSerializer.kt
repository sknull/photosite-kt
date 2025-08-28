package de.visualdigits.photosite.model.rss

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

class DateTimeWithoutMillisSerializer : JsonSerializer<OffsetDateTime>() {

    companion object {

        // roughly based on this idea: https://stackoverflow.com/questions/40487656/jackson-java-time-iso-8601-serialize-without-milliseconds
        private val formatter = DateTimeFormatterBuilder()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral("T")
            .appendValue(ChronoField.HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .optionalStart()
            .appendLiteral(':')
            .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
            .appendOffsetId()
            .toFormatter()
    }

    override fun handledType(): Class<OffsetDateTime> {
        return OffsetDateTime::class.java
    }

    override fun serialize(value: OffsetDateTime, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeString(formatter.format(value))
    }
}
