package de.visualdigits.photosite.domain.service

import de.visualdigits.photosite.domain.data.model.photosite.Photosite
import de.visualdigits.photosite.domain.data.repository.DatabasePageRepository
import de.visualdigits.photosite.domain.data.repository.FilesystemPageRepository
import de.visualdigits.photosite.domain.data.repository.PhotositeRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
class PhotositeService(
    private val photositeRepository: PhotositeRepository,
    private val databasePageRepository: DatabasePageRepository,
    private val filesystemPageRepository: FilesystemPageRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var environment: Environment

    companion object {
        private var photosite: Photosite? = null
    }

    fun getSecrets(): Photosite {
                if (photosite == null) {
                    photosite = photositeRepository.getPhotosite()
                    photosite?.initialize(environment, databasePageRepository, filesystemPageRepository)
                }

                return photosite ?: error("Secrets have not been initialized")
            }
}
