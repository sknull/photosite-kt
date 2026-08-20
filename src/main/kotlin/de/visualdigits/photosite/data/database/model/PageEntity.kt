package de.visualdigits.photosite.data.database.model

import jakarta.persistence.CascadeType
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import java.util.UUID

@Entity
@Table(name = "pages")
data class PageEntity(
    @Id @GeneratedValue(strategy = GenerationType.UUID) val id: UUID? = null,
    val directory: String? = null,
    @Column(unique = true) val path: String,
    val ariaName: String,
    val icon: String? = null,
    val tocName: String? = null,
    val contentType: String? = null,
    val mode: String? = null,
    val speed: Int? = null,
    val pause: Int? = null,
    val download: Boolean? = null,
    val sortBy: String? = null,
    val sortDir: String? = null,
    val sortOrder: String? = null,
    val teaserGoogleMapsName: String? = null,
    val teaserGoogleMapsWidth: String? = null,
    val teaserGoogleMapsHeight: String? = null,
    val teaserGoogleMapsAlign: String? = null,
    val teaserGoogleMapsLat: Double? = null,
    val teaserGoogleMapsLng: Double? = null,
    val teaserGoogleMapsZoom: Int? = null,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "page_id")
    @Fetch(FetchMode.SUBSELECT)
    val images: MutableList<ImageFileEntity> = mutableListOf(),

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "page_id")
    @Fetch(FetchMode.SUBSELECT)
    val teaserTexts: MutableList<TextEntity>? = null,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "page_id")
    @Fetch(FetchMode.SUBSELECT)
    val captions: MutableList<CaptionEntity> = mutableListOf(),

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "page_keywords", joinColumns = [JoinColumn(name = "page_id")])
    @Fetch(FetchMode.SUBSELECT)
    val keywords: MutableList<String> = mutableListOf(),

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "page_id")
    @Fetch(FetchMode.SUBSELECT)
    val paragraphs: MutableList<ParagraphEntity> = mutableListOf(),
    val mdContent: String? = null,
    val htmlContent: String? = null,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "page_id")
    @Fetch(FetchMode.SUBSELECT)
    val translations: MutableList<TranslationEntity> = mutableListOf()
) {
    override fun toString(): String = path
}
