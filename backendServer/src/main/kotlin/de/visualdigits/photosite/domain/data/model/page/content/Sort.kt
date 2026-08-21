package de.visualdigits.photosite.domain.data.model.page.content


data class Sort(
    val by: String? = null,
    val dir: SortDir? = null,
    val order: String? = null
){

    val orderList: MutableList<String> = mutableListOf()

    init {
        if (order != null) {
            orderList.addAll(order.split(",")
                .dropLastWhile { it.isEmpty() }
                .map { s -> s.trim { it <= ' ' } }
            )
        }
    }
}

