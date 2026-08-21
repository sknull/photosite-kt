package de.visualdigits.photosite.domain.model.photosite

import kotlinx.serialization.Serializable

@Serializable
data class SortUi(
    val by: String? = null,
    val dir: SortDir? = null,
    val order: String? = null
)
