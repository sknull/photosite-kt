package de.visualdigits.photosite.domain.data.model.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.UUID

@Serializable
class Translation(
    @Transient val id: UUID? = null,
    @SerialName("lang") val lang: Language? = null,
    @SerialName("alt") val alt: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("title") val title: String? = null
)
