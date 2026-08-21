package de.visualdigits.photosite.presentation.model

import de.visualdigits.photosite.domain.data.serializer.OffsetDateTimeDeserializer
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
data class PageDto(
    val icon: String? = null,
    val tocName: String? = null,
    val content: ContentDto,
    val translations: List<TranslationDto> = listOf(),
    val level: Int = 0,
    val path: String = "/",
    val ariaName: String = "",
    val children: List<PageDto> = listOf(),
    @Serializable(with = OffsetDateTimeDeserializer::class) val lastModified: OffsetDateTime = OffsetDateTime.MIN
)
