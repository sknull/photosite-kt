package de.visualdigits.photosite.model.page.content

import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.Metadata
import com.drew.metadata.exif.ExifSubIFDDirectory
import de.visualdigits.photosite.model.common.KmpOffsetDateTime
import de.visualdigits.photosite.serializer.FileSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.slf4j.LoggerFactory
import java.io.File
import java.time.ZoneOffset
import java.util.TimeZone

@Serializable
class ImageFile(
    @Serializable(with = FileSerializer::class) val file: File,
) {

    private val log = LoggerFactory.getLogger(ImageFile::class.java)

    @Transient
    val name: String = file.getName()

    @Transient
    private var metadata: Metadata? = null

    fun metadata(): Metadata? {
            if (metadata == null) {
                metadata = try {
                    ImageMetadataReader.readMetadata(file)
                } catch (e: Exception) {
                    log.error("Could not extract meta data from file: $file", e)
                    null
                }
            }
            return metadata
        }

    fun lastModified(): KmpOffsetDateTime {
            return metadata()
                ?.getFirstDirectoryOfType(ExifSubIFDDirectory::class.java)
                ?.getDateOriginal(TimeZone.getTimeZone("Europe/Berlin"))
                ?.toInstant()
                ?.atOffset(ZoneOffset.UTC)
                ?.toInstant()
                ?.toEpochMilli()
                ?.let { millis -> KmpOffsetDateTime(millis) }
                ?: KmpOffsetDateTime.MIN
        }
}
