package de.visualdigits.photosite.domain.service

import de.visualdigits.photosite.domain.data.model.common.KmpOffsetDateTime
import de.visualdigits.photosite.domain.data.model.page.Page
import java.util.Locale

abstract class AbstractXmlBaseService(
    protected val photositeService: PhotositeService
) {

    protected fun isoDate(timestamp: KmpOffsetDateTime): String {
        return timestamp.format("yyyy-MM-dd", Locale.US) +
                "T" +
                timestamp.format("HH:mm:ssZZZ", Locale.US)
    }

    protected fun fullDate(timestamp: KmpOffsetDateTime): String {
        return timestamp.format("EEE, dd MMM yyyy HH:mm:ss ZZZ", Locale.US)
    }

    protected fun determinePages(count: Int? = null, filter: ((p: Page) -> Boolean)? = null): List<Page> {
        return photositeService.photosite.pageTree.lastModifiedPages(count, filter)
    }
}
