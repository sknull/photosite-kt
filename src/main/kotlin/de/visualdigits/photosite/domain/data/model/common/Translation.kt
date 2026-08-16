package de.visualdigits.photosite.domain.data.model.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Translation(
    @SerialName("lang") val lang: Language? = null,
    @SerialName("alt") val alt: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("title") val title: String? = null
)
