package de.visualdigits.photosite.data.filesystem.model

import de.visualdigits.photosite.domain.data.model.page.content.SortDir
import kotlinx.serialization.Serializable

@Serializable
data class SortDescriptor(
    val by: String? = null,
    val dir: SortDir? = null,
    val order: String? = null
)
