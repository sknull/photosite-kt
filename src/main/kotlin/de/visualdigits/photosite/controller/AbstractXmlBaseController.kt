package de.visualdigits.photosite.controller

import de.visualdigits.photosite.model.page.Page
import de.visualdigits.photosite.model.siteconfig.Photosite
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


abstract class AbstractXmlBaseController(
    photosite: Photosite
) : AbstractBaseController(photosite) {

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

    protected fun determinePages(count: Int = 0): List<Page> {
        return photosite.pageTree.lastModifiedPages(count)
            .filter { p -> p.images.isNotEmpty() && p.lastModifiedTimestamp > OffsetDateTime.MIN }
    }
}
