package de.visualdigits.photosite.domain.model.photosite

import kotlinx.serialization.Serializable

@Serializable
data class ContentUi(
    val contentType: ContentType? = null,
    val mode: String? = null,
    val speed: Int? = null,
    val pause: Int? = null,
    val download: Boolean? = null,
    val sort: SortUi? = null,
    val teaser: TeaserUi? = null,
    val captions: List<CaptionUi> = listOf(),
    val keywords: List<String> = listOf(),
    val paragraphs: List<ParagraphUi> = listOf(),
    val mdContent: String? = null,
    val htmlContent: String? = null,
    val images: List<ImageFileUi> = listOf()
)
