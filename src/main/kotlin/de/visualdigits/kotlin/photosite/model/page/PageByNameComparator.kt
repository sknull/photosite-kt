package de.visualdigits.kotlin.photosite.model.page


class PageByNameComparator : Comparator<Page?> {
    override fun compare(o1: Page?, o2: Page?): Int {
        var c = 0
        if (o1 == null && o2 == null) {
            return 0
        } else if (o1 == null) {
            return 1
        } else if (o2 == null) {
            return -1
        } else {
            val p1 = o1.parent
            val p2 = o2.parent
            if (p1 == null && p2 == null) {
                return 0
            } else if (p1 == null) {
                return 1
            } else if (p2 == null) {
                return -1
            } else {
                var n1 = p1.name
                var n2 = p2.name
                c = n2?.let { n1?.compareTo(it) }?:0
                if (c == 0) {
                    n1 = o1.name
                    n2 = o2.name
                    c = n2?.let { n1?.compareTo(it) }?:0
                }
            }
        }
        return c
    }
}

