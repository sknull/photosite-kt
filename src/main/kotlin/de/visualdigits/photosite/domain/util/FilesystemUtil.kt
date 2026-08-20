package de.visualdigits.photosite.domain.util

import de.visualdigits.photosite.domain.data.model.photosite.Photosite.Companion.rootDirectory
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
