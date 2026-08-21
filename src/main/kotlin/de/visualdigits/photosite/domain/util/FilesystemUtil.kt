package de.visualdigits.photosite.domain.util

import de.visualdigits.photosite.domain.data.model.photosite.Photosite.Companion.rootDirectory
import de.visualdigits.photosite.domain.data.model.photosite.Photosite.Companion.thumbnailCacheFolder
import net.coobird.thumbnailator.Thumbnails
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Paths

private val log = LoggerFactory.getLogger(FileSystemUtil::class.java)

class FileSystemUtil()

fun getRelativeResourcePath(resource: File): String? {
    return try {
        rootDirectory.toPath()
            .relativize(Paths.get(resource.canonicalPath))
            .toString()
            .replace("\\", "/")
    } catch (e: Exception) {
        log.error("Could not determine relative path for resource '$resource'", e)
        null
    }
}

fun getThumbnailPath(imageFile: File?): String? {
    if (imageFile == null) return null

    val pagetreePath = Paths.get(rootDirectory.canonicalPath, "resources", "pagetree")
    val sourceImageFilePath = Paths.get(imageFile.absolutePath)
    val relativePath = pagetreePath.relativize(sourceImageFilePath).toString()
    val thumbnailFile = Paths.get(thumbnailCacheFolder.canonicalPath, relativePath).toFile()
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

    return getRelativeResourcePath(thumbnailFile)
}
