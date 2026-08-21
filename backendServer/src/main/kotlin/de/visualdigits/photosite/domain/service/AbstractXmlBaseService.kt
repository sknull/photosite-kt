package de.visualdigits.photosite.domain.service

import de.visualdigits.photosite.domain.data.model.page.Page
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

abstract class AbstractXmlBaseService(
    protected val photositeService: PhotositeService
) {
    companion object {
        val FORMAT_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US)
        val FORMAT_TIME = DateTimeFormatter.ofPattern("HH:mm:ssZZZ", Locale.US)
        val FORMAT_DATE_TIME = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss ZZZ", Locale.US)
    }

    protected fun isoDate(timestamp: OffsetDateTime): String {
        return timestamp.format(FORMAT_DATE) +
                "T" +
                timestamp.format(FORMAT_TIME)
    }

    protected fun fullDate(timestamp: OffsetDateTime): String {
        return timestamp.format(FORMAT_DATE_TIME)
    }

    protected fun determinePages(count: Int? = null, filter: ((p: Page) -> Boolean)? = null): List<Page> {
        return photositeService.photosite.pageTree.lastModifiedPages(count, filter)
    }
}
