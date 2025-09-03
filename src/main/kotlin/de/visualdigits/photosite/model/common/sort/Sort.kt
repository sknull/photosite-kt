package de.visualdigits.photosite.model.common.sort

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty


@JsonIgnoreProperties("orderList")
data class Sort(
    @JacksonXmlProperty(isAttribute = true)
    val by: String? = null,

    @JacksonXmlProperty(isAttribute = true)
    val dir: SortDir? = null,

    @JacksonXmlProperty(isAttribute = true)
    val order: String? = null
) {

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

