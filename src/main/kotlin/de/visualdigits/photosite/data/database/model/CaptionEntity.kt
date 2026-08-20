package de.visualdigits.photosite.data.database.model

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import java.util.UUID

@Entity
data class CaptionEntity(
    @Id @GeneratedValue(strategy = GenerationType.UUID) val id: UUID? = null,
    val name: String? = null,
    val alt: String? = null,
    val caption: String? = null,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "translation_id")
    @Fetch(FetchMode.SUBSELECT)
    val translations: MutableList<TranslationEntity> = mutableListOf()
)
