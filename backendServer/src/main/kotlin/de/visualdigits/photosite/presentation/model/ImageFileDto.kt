package de.visualdigits.photosite.presentation.model

import de.visualdigits.photosite.domain.data.serializer.OffsetDateTimeDeserializer
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
data class ImageFileDto(
    val name: String,
    val imagePath: String?,
    val thumbnailPath: String?,
    val apertureValue: String? = null,
    val exposureTime: String? = null,
    val exposureBias: String? = null,
    val isoEquivalent: String? = null,
    val focalLength: String? = null,
    val make: String? = null,
    val model: String? = null,
    val lensModel: String? = null,
    @Serializable(with = OffsetDateTimeDeserializer::class) val lastModified: OffsetDateTime
)
