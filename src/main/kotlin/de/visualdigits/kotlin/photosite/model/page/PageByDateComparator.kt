package de.visualdigits.kotlin.photosite.model.page

import java.time.OffsetDateTime


internal class PageByDateComparator : Comparator<Page?> {
    override fun compare(p1: Page?, p2: Page?): Int {
        var c = 0
        if (p1 == null && p2 == null) {
            return 0
        } else if (p1 == null) {
            return 1
        } else if (p2 == null) {
            return -1
        } else {
            val l1: OffsetDateTime = p1.lastModifiedTimestamp
            val l2: OffsetDateTime = p2.lastModifiedTimestamp
            if (l1 < l2) {
                c = 1
            } else if (l1 > l2) {
                c = -1
            }
        }
        return c
    }
}

