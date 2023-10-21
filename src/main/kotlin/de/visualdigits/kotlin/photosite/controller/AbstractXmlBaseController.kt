package de.visualdigits.kotlin.photosite.controller

import de.visualdigits.kotlin.photosite.model.page.Page
import de.visualdigits.kotlin.photosite.model.siteconfig.navi.PageTree
import java.nio.file.Paths
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*


abstract class AbstractXmlBaseController : AbstractBaseController() {

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

    protected fun determinePageTree(): PageTree {
        return PageTree(
            siteConfigHolder.siteConfig?.site?.rootFolder?.let { rf -> Paths.get(rf, "resources", "pagetree").toFile() }
        )
    }

    protected fun determinePages(): List<Page> {
        return determinePages(determinePageTree(), 0)
    }

    protected fun determinePages(pageTree: PageTree, count: Int): List<Page> {
        return pageTree.getLastModifiedPages(count)
            .filter { p -> p.images.isNotEmpty() && p.lastModifiedTimestamp > OffsetDateTime.MIN }
    }
}
