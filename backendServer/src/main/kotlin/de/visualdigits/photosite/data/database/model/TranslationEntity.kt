package de.visualdigits.photosite.data.database.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.util.UUID

@Entity
class TranslationEntity(
    @Id @GeneratedValue(strategy = GenerationType.UUID) val id: UUID? = null,
    val lang: String? = null,
    val alt: String? = null,
    val name: String? = null,
    val title: String? = null
)
