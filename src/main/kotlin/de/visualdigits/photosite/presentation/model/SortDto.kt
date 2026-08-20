package de.visualdigits.photosite.presentation.model

import de.visualdigits.photosite.domain.data.model.page.content.SortDir
import kotlinx.serialization.Serializable

@Serializable
data class SortDto(
    val `by`: String? = null,
    val dir: SortDir? = null,
    val order: String? = null
)
