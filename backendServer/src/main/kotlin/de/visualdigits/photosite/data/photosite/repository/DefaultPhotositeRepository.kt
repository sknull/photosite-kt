package de.visualdigits.photosite.data.photosite.repository

import de.visualdigits.photosite.data.photosite.mapper.toPhotosite
import de.visualdigits.photosite.data.photosite.model.PhotositeDescriptor
import de.visualdigits.photosite.domain.data.model.photosite.Photosite
import de.visualdigits.photosite.domain.data.repository.PhotositeRepository
import de.visualdigits.photosite.domain.util.jsonMapper
import org.springframework.stereotype.Service
import java.io.File

@Service
class DefaultPhotositeRepository : PhotositeRepository {

    override fun getPhotosite(): Photosite {
        return jsonMapper
            .decodeFromString<PhotositeDescriptor>(File("${Photosite.rootDirectory}/secrets/secrets.json").readText())
            .toPhotosite()
    }
}
