package de.visualdigits.photosite.model.page

import java.time.OffsetDateTime


internal class PageByDateComparator : Comparator<Page?> {
    override fun compare(p1: Page?, p2: Page?): Int {
        var c = 0
        when {
            p1 == null && p2 == null -> {
                return 0
            }
            p1 == null -> {
                return 1
            }
            p2 == null -> {
                return -1
            }
            else -> {
                val l1: OffsetDateTime = p1.content.lastModified()
                val l2: OffsetDateTime = p2.content.lastModified()
                if (l1 < l2) {
                    c = 1
                } else if (l1 > l2) {
                    c = -1
                }
            }
        }
        return c
    }
}

