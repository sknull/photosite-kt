package de.visualdigits.photosite.model.pagemodern

import com.fasterxml.jackson.annotation.JsonAlias
import java.time.OffsetDateTime


class Content(
    @JsonAlias("plugin", "contentType") var contentType: ContentType? = null,
    val mode: String? = null,
    val speed: Int? = null,
    val pause: Int? = null,
    val download: Boolean? = null,
    val sort: Sort? = null,
    val teaser: Teaser? = null,
    var captions: List<Caption> = listOf(),
    val keywords: String? = null,
    val paragraphs: List<Paragraph> = listOf()
) {
    var mdContent: String? = null
    var htmlContent: String? = null
    var images: List<ImageFile> = listOf()
    var lastModifiedTimestamp: OffsetDateTime = OffsetDateTime.MIN

    lateinit var captionsMap: Map<String, Caption>

    init {
        captions = captions.filter { c -> c.name?.isNotBlank() == true }
        captionsMap = captions.associate { c -> Pair(c.name!!, c) }
    }
}
