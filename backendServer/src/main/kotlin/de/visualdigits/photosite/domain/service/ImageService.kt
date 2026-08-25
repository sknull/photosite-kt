package de.visualdigits.photosite.domain.service

import de.visualdigits.photosite.domain.data.model.page.content.ImageFile
import de.visualdigits.photosite.domain.util.getThumbnailPath
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ImageService {

    private val log = LoggerFactory.getLogger(javaClass)

    fun getThumbnail(image: ImageFile): String? {
        return getThumbnailPath(image.file)
    }
}
