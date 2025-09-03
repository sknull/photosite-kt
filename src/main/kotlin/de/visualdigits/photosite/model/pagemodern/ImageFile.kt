package de.visualdigits.photosite.model.pagemodern

import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.Metadata
import com.drew.metadata.exif.ExifSubIFDDirectory
import org.slf4j.LoggerFactory
import java.io.File
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.TimeZone


class ImageFile(
    val file: File,
) {

    private val log = LoggerFactory.getLogger(ImageFile::class.java)

    val name: String = file.getName()

    var metadata: Metadata? = null

    var lastModified: OffsetDateTime = OffsetDateTime.MIN

    init {
        if (metadata == null) {
            try {
                metadata = ImageMetadataReader.readMetadata(file)
            } catch (e: Exception) {
                log.error("Could not extract meta data from file: $file", e)
            }
            lastModified = metadata
                ?.getFirstDirectoryOfType(ExifSubIFDDirectory::class.java)
                ?.getDateOriginal(TimeZone.getTimeZone("Europe/Berlin"))
                ?.toInstant()?.atOffset(ZoneOffset.UTC)
                ?: Instant.ofEpochMilli(file.lastModified()).atOffset(ZoneOffset.UTC)
        }
    }
}

