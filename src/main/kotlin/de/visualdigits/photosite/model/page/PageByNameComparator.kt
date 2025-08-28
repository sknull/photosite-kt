package de.visualdigits.photosite.model.page


class PageByNameComparator : Comparator<Page?> {
    override fun compare(o1: Page?, o2: Page?): Int {
        var c: Int
        when {
            o1 == null && o2 == null -> {
                return 0
            }
            o1 == null -> {
                return 1
            }
            o2 == null -> {
                return -1
            }
            else -> {
                val p1 = o1.parent
                val p2 = o2.parent
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
            }
        }
        return c
    }
}

