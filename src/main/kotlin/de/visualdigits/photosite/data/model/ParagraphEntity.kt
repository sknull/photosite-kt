package de.visualdigits.photosite.data.model

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
data class ParagraphEntity(
    @Id @GeneratedValue(strategy = GenerationType.UUID) val id: UUID? = null,
    val imageName: String? = null,
    val imageAlign: String? = null,
    val imageAlt: String? = null,
    val googleMapsName: String? = null,
    val googleMapsWidth: String? = null,
    val googleMapsHeight: String? = null,
    val googleMapsAlign: String? = null,
    val googleMapsLat: Double? = null,
    val googleMapsLng: Double? = null,
    val googleMapsZoom: Int? = null,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "text_id")
    @Fetch(FetchMode.SUBSELECT)
    val texts: MutableList<TextEntity>? = null
)
