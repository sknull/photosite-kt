package de.visualdigits.photosite.data.photosite.model.plugins

import de.visualdigits.photosite.domain.data.model.page.content.ContentType
import kotlinx.serialization.Serializable

@Serializable
data class HtmlContentDescriptor(
    val contentType: ContentType = ContentType.Html
)
