package de.visualdigits.photosite.domain.data.model.page.content

import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.Metadata
import com.drew.metadata.exif.ExifIFD0Directory
import com.drew.metadata.exif.ExifSubIFDDescriptor
import com.drew.metadata.exif.ExifSubIFDDirectory
import de.visualdigits.photosite.domain.data.model.common.KmpOffsetDateTime
import de.visualdigits.photosite.domain.data.serializer.FileSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.slf4j.LoggerFactory
import java.io.File
import java.time.ZoneOffset
import java.util.TimeZone
import java.util.UUID

@Serializable
class ImageFile(
    @Transient val id: UUID? = null,
    @Serializable(with = FileSerializer::class) val file: File,

    @Transient var name: String = file.name,
    @Transient var apertureValue: String? = null,
    @Transient var exposureTime: String? = null,
    @Transient var exposureBias: String? = null,
    @Transient var isoEquivalent: String? = null,
    @Transient var focalLength: String? = null,
    @Transient var make: String? = null,
    @Transient var model: String? = null,
    @Transient var lensModel: String? = null,
    @Transient var lastModified: KmpOffsetDateTime = KmpOffsetDateTime(file.lastModified())
) {

    private val log = LoggerFactory.getLogger(ImageFile::class.java)

    fun initiaslizeMetadata() {
        val metadata: Metadata? = try {
            ImageMetadataReader.readMetadata(file)
        } catch (e: Exception) {
            log.error("Could not extract meta data from file: $file", e)
            null
        }
        val exifDir = metadata?.getFirstDirectoryOfType(ExifIFD0Directory::class.java)
        val exifSubDir = metadata?.getFirstDirectoryOfType(ExifSubIFDDirectory::class.java)
        val exifSub = ExifSubIFDDescriptor(exifSubDir)
        if (exifDir != null && exifSubDir != null) {
            apertureValue = exifSub.getApertureValueDescription()
            exposureTime = exifSub.getExposureTimeDescription()
            exposureBias = exifSub.getExposureBiasDescription()
            isoEquivalent = exifSub.getIsoEquivalentDescription()
            focalLength = exifSub.getFocalLengthDescription()
            make = exifDir.getString(ExifIFD0Directory.TAG_MAKE)
            model = exifDir.getString(ExifIFD0Directory.TAG_MODEL)
            lensModel = exifSubDir.getString(ExifSubIFDDirectory.TAG_LENS_MODEL)
            lastModified = exifSubDir
                .getDateOriginal(TimeZone.getTimeZone("Europe/Berlin"))
                ?.toInstant()
                ?.atOffset(ZoneOffset.UTC)
                ?.toInstant()
                ?.toEpochMilli()
                ?.let { millis -> KmpOffsetDateTime(millis) }
                ?: KmpOffsetDateTime.MIN
        }
    }
}
