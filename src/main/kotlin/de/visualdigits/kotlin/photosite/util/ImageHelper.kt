package de.visualdigits.kotlin.photosite.util

import de.visualdigits.kotlin.photosite.model.common.ImageFile
import de.visualdigits.kotlin.photosite.model.siteconfig.SiteConfig
import net.coobird.thumbnailator.Thumbnails
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Paths

object ImageHelper {

    private val log = LoggerFactory.getLogger(ImageHelper::class.java)

    fun getThumbnail(siteConfig: SiteConfig, image: ImageFile): String? {
        val site = siteConfig.site
        val pagetreePath = site.rootFolder?.let { Paths.get(it, site.resourcesRoot, "pagetree") }
        val imageFile: File = image.file
        val sourceImageFilePath = Paths.get(imageFile.absolutePath)
        val relativePath = pagetreePath?.relativize(sourceImageFilePath).toString()
        val thumbnailFile = site.thumbnailCacheFolder?.let  { Paths.get(File(it).canonicalPath, relativePath).toFile() }
        val thumbnailFolder = thumbnailFile?.getParentFile()
        if (thumbnailFolder?.exists() != true && thumbnailFolder?.mkdirs() != true) {
            log.error("Could not create thumbnail folder '$thumbnailFolder'")
        }
        if (thumbnailFile?.exists() != true) {
            runCatching {
                Thumbnails.of(imageFile)
                    .size(128, 128)
                    .keepAspectRatio(true)
                    .toFile(thumbnailFile)
            }.onFailure { e ->
                log.error("Could note create thumbnail for image '" + imageFile.absolutePath + "'", e)
            }
        }
        return thumbnailFile?.let { siteConfig.getRelativeResourcePath(it) }
    }
}
