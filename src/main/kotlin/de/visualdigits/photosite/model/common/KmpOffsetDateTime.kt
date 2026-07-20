package de.visualdigits.photosite.model.common

import de.visualdigits.photosite.serializer.KmpOffsetDateTimeHeuristicDeserializer
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.asTimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.offsetIn
import kotlinx.datetime.parse
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant

@Serializable(with = KmpOffsetDateTimeHeuristicDeserializer::class)
data class KmpOffsetDateTime(
    val instant: Instant,
    val offset: UtcOffset = UtcOffset.ZERO
) : Comparable<KmpOffsetDateTime> {

    constructor(millis: Long, offsetString: String) : this(
        instant = Instant.fromEpochMilliseconds(millis),
        offset = UtcOffset.parse(offsetString)
    )

    constructor(millis: Long, offset: UtcOffset = UtcOffset.ZERO) : this(
        instant = Instant.fromEpochMilliseconds(millis),
        offset = offset
    )

    companion object {

        val MIN: KmpOffsetDateTime = KmpOffsetDateTime(
            instant = Instant.DISTANT_PAST,
            offset = UtcOffset(hours = 18) // Maximales positives Java-Offset für die absolut früheste lokale Zeit
        )

        val MAX: KmpOffsetDateTime = KmpOffsetDateTime(
            instant = Instant.DISTANT_FUTURE,
            offset = UtcOffset(hours = -18) // Maximales negatives Java-Offset für die absolut späteste lokale Zeit
        )

        fun now(): KmpOffsetDateTime = KmpOffsetDateTime(instant = Clock.System.now())

        fun ofInstant(
            instant: Instant,
            offset: UtcOffset): KmpOffsetDateTime =
                KmpOffsetDateTime(instant, offset)

        fun parse(input: String, format: DateTimeFormat<DateTimeComponents>): KmpOffsetDateTime {
            return KmpOffsetDateTime(Instant.parse(input, format))
        }

        fun systemDefaultOffset(): UtcOffset {
            val currentInstant = Clock.System.now() // Current time in timeline
            val systemTimeZone = TimeZone.currentSystemDefault()

            // Determines the offset for this moment in time
            return currentInstant.offsetIn(systemTimeZone)
        }

        fun fromString(isoDateTime: String) : KmpOffsetDateTime {
            var input = isoDateTime.replaceFirst(" ", "T").substringBefore(" +")
            if (!input.endsWith("Z")) {
                input += "Z"
            }
            return try {
                KmpOffsetDateTime(
                    instant = Instant.parse(input),
                    offset = UtcOffset.ZERO
                )
            } catch (e: Exception) {
                throw IllegalStateException("Could not parse utc time: '$input'", e)
            }
        }
    }

    fun toLocalDateTime(): LocalDateTime = instant.toLocalDateTime(offset.asTimeZone())

    fun toLocalDateTimeInSystemTimezone(): LocalDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

    fun toInstant(): Instant = instant

    fun format(pattern: String, locale: Locale = Locale.getDefault()): String {
        return this.formatLocalized(pattern, locale)
    }

    fun format(format: DateTimeFormat<DateTimeComponents>): String {
        return this.instant.format(format)
    }

    fun minus(other: KmpOffsetDateTime): Duration {
        return this.instant.minus(other.instant)
    }

    fun isBefore(other: KmpOffsetDateTime): Boolean {
        return this.instant < other.instant
    }

    fun minus(duration: Duration): KmpOffsetDateTime {
        return KmpOffsetDateTime(this.instant.minus(duration))
    }

    val year: Int
        get() = instant.toLocalDateTime(offset.asTimeZone()).year

    val month: Int
        get() = instant.toLocalDateTime(offset.asTimeZone()).month.ordinal

    val day: Int
        get() = instant.toLocalDateTime(offset.asTimeZone()).day

    val hour: Int
        get() = instant.toLocalDateTime(offset.asTimeZone()).hour

    val minute: Int
        get() = instant.toLocalDateTime(offset.asTimeZone()).minute

    val second: Int
        get() = instant.toLocalDateTime(offset.asTimeZone()).second

    val millisecond: Int
        get() = instant.toLocalDateTime(offset.asTimeZone()).nanosecond / 1000

    override fun toString(): String {
        return if (offset == UtcOffset.ZERO) {
            instant.toString()
        } else {
            val instantWithoutZ = instant.toString().substringBeforeLast("Z")
            "$instantWithoutZ$offset"
        }
    }

    override fun compareTo(other: KmpOffsetDateTime): Int {
        return compareBy<KmpOffsetDateTime>({ it.instant }).compare(this, other)
    }
}

fun LocalDateTime.format(pattern: String): String {
    @OptIn(FormatStringsInDatetimeFormats::class)
    val dateTimeFormat = LocalDateTime.Format {
        byUnicodePattern(pattern)
    }
    return dateTimeFormat.format(this)
}

fun KmpOffsetDateTime.formatLocalized(pattern: String, locale: Locale = Locale.getDefault()): String {
    // 1. Konvertierung in Java-Typen
    val javaInstant = java.time.Instant.ofEpochMilli(this.instant.toEpochMilliseconds())
    val javaOffset = ZoneOffset.ofTotalSeconds(this.offset.totalSeconds)
    val javaOffsetDateTime = java.time.OffsetDateTime.ofInstant(javaInstant, javaOffset)

    // 2. Formatierung über die aktuelle Systemsprache des Geräts
    val formatter = DateTimeFormatter.ofPattern(pattern, locale)
    return javaOffsetDateTime.format(formatter)
}
