package de.visualdigits.photosite.presentation.model

import de.visualdigits.photosite.domain.data.model.page.content.ContentType
import kotlinx.serialization.Serializable

@Serializable
data class ContentDto(
    val contentType: ContentType? = null,
    val mode: String? = null,
    val speed: Int? = null,
    val pause: Int? = null,
    val download: Boolean? = null,
    val sort: SortDto? = null,
    val teaser: TeaserDto? = null,
    val captions: List<CaptionDto> = listOf(),
    val keywords: List<String> = listOf(),
    val paragraphs: List<ParagraphDto> = listOf(),
    val mdContent: String? = null,
    val htmlContent: String? = null,
    val images: List<ImageFileDto> = listOf()
)
