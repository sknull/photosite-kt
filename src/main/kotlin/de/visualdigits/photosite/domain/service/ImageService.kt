package de.visualdigits.photosite.domain.service

import de.visualdigits.photosite.domain.data.model.page.content.ImageFile
import de.visualdigits.photosite.domain.data.model.photosite.Photosite
import de.visualdigits.photosite.domain.util.getRelativeResourcePath
import de.visualdigits.photosite.domain.util.getThumbnailPath
import net.coobird.thumbnailator.Thumbnails
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.Paths

@Service
class ImageService {

    private val log = LoggerFactory.getLogger(javaClass)

    fun getThumbnail(image: ImageFile): String? {
        return getThumbnailPath(image.file)
    }
}
