package de.visualdigits.photosite.domain.data.model.page.content

import kotlinx.serialization.Serializable


@Serializable
data class Sort(
    val `by`: String? = null,
    val dir: SortDir? = null,
    val order: String? = null
){

    @Transient
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

