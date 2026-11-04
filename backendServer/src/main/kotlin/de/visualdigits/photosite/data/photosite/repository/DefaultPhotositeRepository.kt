package de.visualdigits.photosite.data.photosite.repository

import de.visualdigits.photosite.data.photosite.mapper.toPhotosite
import de.visualdigits.photosite.data.photosite.model.PhotositeDescriptor
import de.visualdigits.photosite.domain.data.model.photosite.Photosite
import de.visualdigits.photosite.domain.data.repository.PhotositeRepository
import de.visualdigits.photosite.domain.util.jsonMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File

@Service
class DefaultPhotositeRepository : PhotositeRepository {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun getPhotosite(): Photosite {
        val file = File("${Photosite.rootDirectory}/secrets/secrets.json")
        val json = file.readText()
        return jsonMapper
            .decodeFromString<PhotositeDescriptor>(json)
            .toPhotosite()
    }
}
