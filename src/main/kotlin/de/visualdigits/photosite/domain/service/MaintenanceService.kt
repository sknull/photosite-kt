package de.visualdigits.photosite.domain.service

import de.visualdigits.photosite.Application
import de.visualdigits.photosite.domain.data.model.page.Page
import de.visualdigits.photosite.domain.data.model.page.content.ImageFile
import de.visualdigits.photosite.domain.data.model.photosite.Photosite
import de.visualdigits.photosite.domain.data.repository.PageRepository
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.Paths
import java.time.LocalDateTime
import java.util.Locale

@Service
class MaintenanceService(
    private val photosite: Photosite,
    private val domainCertificatesService: DomainCertificatesService,
    private val imageService: ImageService,
    private val pageRepository: PageRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var environment: Environment

    fun checkCerts(
        forceUpdate: Boolean,
        response: HttpServletResponse
    ) {
        if (environment.activeProfiles.contains("prod")) {
            val newExpiryDate = domainCertificatesService.maintainServerCertificate(
                domain = photosite.domain!!,
                certbotUri = photosite.ssl!!.certbotUri!!,
                certbotAlias = photosite.ssl!!.keyAlias!!,
                certbotPassword = photosite.ssl!!.keyStorePassword!!,
                forceUpdate = forceUpdate,
                gracePeriod = 1
            )
            if (newExpiryDate.isAfter(LocalDateTime.now())) {
                Application.restart("ssl")
            } else {
                response.sendRedirect("/")
            }
        }
    }

    fun createThumbnails(response: HttpServletResponse) {
        createThumbnails(Paths.get(Photosite.rootDirectory.canonicalPath, "resources", "pagetree").toFile())
        response.sendRedirect("/")
    }

    fun reloadPageTree(response: HttpServletResponse) {
        Page.readPagetreeFromFilesystem(pageRepository)
        response.sendRedirect("/")
    }

    private fun createThumbnails(rootFolder: File) {
        log.info("Creating thumbnails for folder: $rootFolder")
        rootFolder.listFiles { file: File ->
            file.isFile() && file.getName().lowercase(Locale.getDefault()).endsWith(".jpg")
        }?.forEach { imageFile ->
            imageService.getThumbnail(ImageFile(file = imageFile))
        }

        // recurse into sub folders
        rootFolder
            .listFiles { obj: File -> obj.isDirectory() }
            ?.forEach { rf -> createThumbnails(rf) }
    }
}
