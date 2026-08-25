package de.visualdigits.photosite.data.database.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.util.UUID

@Entity
data class ImageFileEntity(
    @Id @GeneratedValue(strategy = GenerationType.UUID) val id: UUID? = null,
    val file: String,
    val imagePath: String?,
    val thumbnailPath: String?,
    val name: String,
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
    val lastModified: String
)
