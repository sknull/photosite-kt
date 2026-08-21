package de.visualdigits.photosite.data.filesystem.model

import de.visualdigits.photosite.domain.data.model.page.content.ContentType
import de.visualdigits.photosite.domain.data.model.page.content.Paragraph
import de.visualdigits.photosite.domain.data.model.page.content.Sort
import de.visualdigits.photosite.domain.data.model.page.content.Teaser
import kotlinx.serialization.Serializable

@Serializable
data class ContentDescriptor(
    val contentType: ContentType = ContentType.None,
    val mode: String? = null,
    val speed: Int? = null,
    val pause: Int? = null,
    val download: Boolean? = null,
    val sort: SortDescriptor? = null,
    val teaser: TeaserDescriptor? = null,
    val captions: List<CaptionDescriptor> = listOf(),
    val keywords: List<String> = listOf(),
    val paragraphs: List<ParagraphDescriptor> = listOf(),
    val mdContent: String? = null,
    val htmlContent: String? = null,
)
