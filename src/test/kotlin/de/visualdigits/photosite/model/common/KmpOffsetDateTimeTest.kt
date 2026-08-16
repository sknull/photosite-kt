package de.visualdigits.photosite.model.common

import de.visualdigits.photosite.domain.data.model.common.KmpOffsetDateTime
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.time.Instant

class KmpOffsetDateTimeTest {

    @Test
    fun testCompare() {
        val td1 = KmpOffsetDateTime(Instant.parse("2026-01-01T10:00:00.000000000Z"))
        val td2 = KmpOffsetDateTime(Instant.parse("2026-06-01T10:00:00.000000000Z"))
        assertTrue(td2 > td1)
        assertFalse(td2 < td1)

        assertTrue(td1 > KmpOffsetDateTime.MIN)
        assertTrue(td2 > KmpOffsetDateTime.MIN)

        assertTrue(td1 < KmpOffsetDateTime.MAX)
        assertTrue(td2 < KmpOffsetDateTime.MAX)
    }
}
