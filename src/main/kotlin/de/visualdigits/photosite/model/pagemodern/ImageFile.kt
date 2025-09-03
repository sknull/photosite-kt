package de.visualdigits.photosite.model.pagemodern

import com.drew.imaging.ImageMetadataReader
import com.drew.imaging.ImageProcessingException
import com.drew.metadata.Metadata
import com.drew.metadata.exif.ExifSubIFDDirectory
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.util.Date
import java.util.TimeZone


class ImageFile(
    val file: File,
    var date: Date? = null
) {

    private val log = LoggerFactory.getLogger(ImageFile::class.java)

    var metadata: Metadata? = null

    init {
//        metadata = readMetaData(file)
        val exifSubDir = metadata?.getFirstDirectoryOfType(
            ExifSubIFDDirectory::class.java
        )
        if (exifSubDir != null) {
            date = exifSubDir.getDateOriginal(TimeZone.getTimeZone("Europe/Berlin"))
        }
        if (date == null) {
            date = Date(file.lastModified())
        }
    }

    val name: String = file.getName()

    private fun readMetaData(file: File): Metadata? {
        var metadata: Metadata? = null
        try {
            metadata = ImageMetadataReader.readMetadata(file)
        } catch (e: IOException) {
            log.error("Could not extract meta data from file: $file", e)
        } catch (e: ImageProcessingException) {
            log.error("Could not extract meta data from file: $file", e)
        }
        return metadata
    }
}

