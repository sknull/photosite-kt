package de.visualdigits.photosite.util

import de.visualdigits.photosite.model.page.ImageFile
import de.visualdigits.photosite.model.siteconfig.Photosite
import net.coobird.thumbnailator.Thumbnails
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Paths

object ImageHelper {

    private val log = LoggerFactory.getLogger(ImageHelper::class.java)

    fun getThumbnail(image: ImageFile): String? {
        val pagetreePath = Paths.get(Photosite.rootDirectory.canonicalPath, "resources", "pagetree")
        val imageFile: File = image.file
        val sourceImageFilePath = Paths.get(imageFile.absolutePath)
        val relativePath = pagetreePath.relativize(sourceImageFilePath).toString()
        val thumbnailFile = Paths.get(Photosite.thumbnailCacheFolder.canonicalPath, relativePath).toFile()
        val thumbnailFolder = thumbnailFile.parentFile
        if (thumbnailFolder?.exists() != true && thumbnailFolder?.mkdirs() != true) {
            log.error("Could not create thumbnail folder '$thumbnailFolder'")
        }
        if (!thumbnailFile.exists()) {
            runCatching {
                Thumbnails.of(imageFile)
                    .size(128, 128)
                    .keepAspectRatio(true)
                    .toFile(thumbnailFile)
            }.onFailure { e ->
                log.error("Could note create thumbnail for image '" + imageFile.absolutePath + "'", e)
            }
        }
        return Photosite.getRelativeResourcePath(thumbnailFile)
    }
}
