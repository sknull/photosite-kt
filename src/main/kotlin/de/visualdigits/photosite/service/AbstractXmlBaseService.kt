package de.visualdigits.photosite.service

import de.visualdigits.photosite.model.page.Page
import de.visualdigits.photosite.model.photosite.Photosite
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

abstract class AbstractXmlBaseService(
    protected val photosite: Photosite
) {

    protected fun isoDate(timestamp: OffsetDateTime): String {
        return timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US)) +
                "T" +
                timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ssZZZ", Locale.US))
    }

    protected fun fullDate(timestamp: OffsetDateTime): String {
        return timestamp.format(
            DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss ZZZ", Locale.US)
        )
    }

    protected fun determinePages(count: Int? = null, filter: ((p: Page) -> Boolean)? = null): List<Page> {
        return photosite.pageTree.lastModifiedPages(count, filter)
    }
}
