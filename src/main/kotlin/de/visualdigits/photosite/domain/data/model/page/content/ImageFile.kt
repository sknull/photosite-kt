package de.visualdigits.photosite.domain.data.model.page.content

import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.Metadata
import com.drew.metadata.exif.ExifIFD0Directory
import com.drew.metadata.exif.ExifSubIFDDescriptor
import com.drew.metadata.exif.ExifSubIFDDirectory
import de.visualdigits.photosite.domain.data.model.common.KmpOffsetDateTime
import org.slf4j.LoggerFactory
import java.io.File
import java.time.ZoneOffset
import java.util.TimeZone
import java.util.UUID

class ImageFile(
    val id: UUID? = null,
    val file: File,

    var name: String = file.name,
    var apertureValue: String? = null,
    var exposureTime: String? = null,
    var exposureBias: String? = null,
    var isoEquivalent: String? = null,
    var focalLength: String? = null,
    var make: String? = null,
    var model: String? = null,
    var lensModel: String? = null,
    var lastModified: KmpOffsetDateTime = KmpOffsetDateTime(file.lastModified())
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
