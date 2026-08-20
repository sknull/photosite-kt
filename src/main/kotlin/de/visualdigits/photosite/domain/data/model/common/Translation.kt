package de.visualdigits.photosite.domain.data.model.common

import java.util.UUID

class Translation(
    val id: UUID? = null,
    val lang: Language = Language.DE,
    val name: String? = null,
    val alt: String? = null,
    val title: String? = null
)
