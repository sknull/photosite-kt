package de.visualdigits.photosite.domain.service

import de.visualdigits.photosite.domain.data.model.photosite.Photosite
import de.visualdigits.photosite.domain.data.repository.DatabasePageRepository
import de.visualdigits.photosite.domain.data.repository.FilesystemPageRepository
import de.visualdigits.photosite.domain.data.repository.PhotositeRepository
import de.visualdigits.photosite.domain.util.jsonMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import java.io.File

@Service
class PhotositeService(
    private val photositeRepository: PhotositeRepository,
    private val databasePageRepository: DatabasePageRepository,
    private val filesystemPageRepository: FilesystemPageRepository
) {

    @Autowired
    private lateinit var environment: Environment

    companion object {
        private var photosite: Photosite? = null
    }

    val photosite: Photosite
            get() {
                if (PhotositeService.photosite == null) {
                    PhotositeService.photosite = photositeRepository.getPhotosite()
                    PhotositeService.photosite!!.initialize(environment, databasePageRepository, filesystemPageRepository)
                }

                return PhotositeService.photosite!!
            }
}
