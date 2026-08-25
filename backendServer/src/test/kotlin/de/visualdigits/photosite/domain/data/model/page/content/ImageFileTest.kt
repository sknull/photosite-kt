package de.visualdigits.photosite.domain.data.model.page.content

import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.exif.ExifIFD0Directory
import com.drew.metadata.exif.ExifSubIFDDescriptor
import com.drew.metadata.exif.ExifSubIFDDirectory
import com.drew.metadata.jpeg.JpegDirectory
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable
import java.io.File

@DisabledIfEnvironmentVariable(named = "CI", matches = "true")
class ImageFileTest {

    @Test
    fun testIterateMetadata() {
        val file = File("C:\\Users\\sknull\\.photosite\\resources\\pagetree\\Arts\\Cinema 4D\\Audi_TT.jpg")
        val metadata = ImageMetadataReader.readMetadata(file)
        metadata.directories.forEach { directory ->
            println("${directory.name}")
            directory.tags.forEach { tag ->
                println("- $tag")
            }
        }
    }

    @Test
    fun testMetadata() {
        val file = File("C:\\Users\\sknull\\.photosite\\resources\\pagetree\\Fotos\\Unterwegs\\Deutschland\\Hamburg\\Architektur\\U2 Station Horner Rennbahn\\_1034818.jpg")
        val metadata = ImageMetadataReader.readMetadata(file)
        val jpgDir = metadata?.getFirstDirectoryOfType(JpegDirectory::class.java)
        val exifDir = metadata?.getFirstDirectoryOfType(ExifIFD0Directory::class.java)
        val exifSubDir = metadata?.getFirstDirectoryOfType(ExifSubIFDDirectory::class.java)
        val exifSub = ExifSubIFDDescriptor(exifSubDir)
        if (jpgDir != null && exifDir != null && exifSubDir != null) {
            val width = jpgDir.imageWidth
            val height = jpgDir.imageHeight
            val apertureValue = exifSub.getApertureValueDescription()
            val exposureTime = exifSub.getExposureTimeDescription()
            val exposureBias = exifSub.getExposureBiasDescription()
            val isoEquivalent = exifSub.getIsoEquivalentDescription()
            val focalLength = exifSub.getFocalLengthDescription()
            val make = exifDir.getString(ExifIFD0Directory.TAG_MAKE)
            val model = exifDir.getString(ExifIFD0Directory.TAG_MODEL)
            val lensModel = exifSubDir.getString(ExifSubIFDDirectory.TAG_LENS_MODEL)

            println(width)
            println(height)
            println(apertureValue)
            println(exposureTime)
            println(exposureBias)
            println(isoEquivalent)
            println(focalLength)
            println(make)
            println(model)
            println(lensModel)
        }
    }
}
