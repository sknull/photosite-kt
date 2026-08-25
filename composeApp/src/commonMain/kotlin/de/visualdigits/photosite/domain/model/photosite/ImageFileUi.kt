package de.visualdigits.photosite.domain.model.photosite

import de.visualdigits.common.domain.model.common.KmpOffsetDateTime
import kotlinx.serialization.Serializable

@Serializable
data class ImageFileUi(
    val name: String,
    val imagePath: String?,
    val thumbnailPath: String?,
    var width: Int = 0,
    var height: Int = 0,
    val apertureValue: String? = null,
    val exposureTime: String? = null,
    val exposureBias: String? = null,
    val isoEquivalent: String? = null,
    val focalLength: String? = null,
    val make: String? = null,
    val model: String? = null,
    val lensModel: String? = null,
    val lastModified: KmpOffsetDateTime
)
