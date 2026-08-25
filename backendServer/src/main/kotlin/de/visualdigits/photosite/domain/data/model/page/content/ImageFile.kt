package de.visualdigits.photosite.domain.data.model.page.content

import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.Metadata
import com.drew.metadata.exif.ExifIFD0Directory
import com.drew.metadata.exif.ExifSubIFDDescriptor
import com.drew.metadata.exif.ExifSubIFDDirectory
import com.drew.metadata.jpeg.JpegDirectory
import de.visualdigits.photosite.domain.util.getRelativeResourcePath
import de.visualdigits.photosite.domain.util.getThumbnailPath
import org.slf4j.LoggerFactory
import java.io.File
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.TimeZone
import java.util.UUID
import javax.imageio.ImageIO

class ImageFile(
    val id: UUID? = null,
    val file: File,
    val imagePath: String? = getRelativeResourcePath(file),
    val thumbnailPath: String? = getThumbnailPath(file),

    var name: String = file.name,

    var width: Int = 0,
    var height: Int = 0,

    var apertureValue: String? = null,
    var exposureTime: String? = null,
    var exposureBias: String? = null,
    var isoEquivalent: String? = null,
    var focalLength: String? = null,
    var make: String? = null,
    var model: String? = null,
    var lensModel: String? = null,
    var lastModified: OffsetDateTime = OffsetDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()), ZoneOffset.UTC)
) {

    private val log = LoggerFactory.getLogger(ImageFile::class.java)

    fun initializeMetadata() {
        val metadata: Metadata? = try {
            ImageMetadataReader.readMetadata(file)
        } catch (e: Exception) {
            log.error("Could not extract meta data from file: $file", e)
            null
        }
        val exifDir = metadata?.getFirstDirectoryOfType(ExifIFD0Directory::class.java)
        if (exifDir != null) {
            make = exifDir.getString(ExifIFD0Directory.TAG_MAKE)
            model = exifDir.getString(ExifIFD0Directory.TAG_MODEL)
        }

        val exifSubDir = metadata?.getFirstDirectoryOfType(ExifSubIFDDirectory::class.java)
        if (exifSubDir != null) {
            lensModel = exifSubDir.getString(ExifSubIFDDirectory.TAG_LENS_MODEL)
            lastModified = exifSubDir
                .getDateOriginal(TimeZone.getDefault())
                ?.toInstant()
                ?.atOffset(ZoneOffset.UTC)
                ?: OffsetDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()), ZoneOffset.UTC)
        }

        val exifSub = exifSubDir?.let { ExifSubIFDDescriptor(it) }
        if (exifSub != null) {
            apertureValue = exifSub.getApertureValueDescription()
            apertureValue = exifSub.getApertureValueDescription()
            exposureTime = exifSub.getExposureTimeDescription()
            exposureBias = exifSub.getExposureBiasDescription()
            isoEquivalent = exifSub.getIsoEquivalentDescription()
            focalLength = exifSub.getFocalLengthDescription()
        }

        val jpgDir = metadata?.getFirstDirectoryOfType(JpegDirectory::class.java)
        if (jpgDir != null) {
            width = jpgDir.imageWidth
            height = jpgDir.imageHeight
            if (width == 0 && height == 0) {
                val image = ImageIO.read(file)
                width = image.width
                height = image.height
            }
        }
    }
}
