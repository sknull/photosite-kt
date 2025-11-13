package de.visualdigits.photosite.service

import de.visualdigits.photosite.Application
import de.visualdigits.photosite.controller.internal.MaintenanceController
import de.visualdigits.photosite.model.page.content.ImageFile
import de.visualdigits.photosite.model.photosite.Photosite
import jakarta.annotation.PostConstruct
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.Paths
import java.time.LocalDateTime
import java.util.Locale

@Service
class MaintenanceService(
    private val photosite: Photosite,
    private val domainCertificatesService: DomainCertificatesService,
    private val imageService: ImageService
) {

    private val log = LoggerFactory.getLogger(MaintenanceController::class.java)

    private lateinit var expiryDate: LocalDateTime


    @PostConstruct
    fun initialize() {
        expiryDate = domainCertificatesService.determineExpiryDate(photosite.ssl!!.keyAlias!!, photosite.ssl!!.keyStorePassword!!)
    }


    fun checkCerts(
        forceUpdate: Boolean,
        response: HttpServletResponse
    ) {
        if (photosite.isProfileActive("prod")) {
            val newExpiryDate = domainCertificatesService.maintainServerCertificate(
                certbotUri = photosite.ssl!!.certbotUri!!,
                certbotAlias = photosite.ssl!!.keyAlias!!,
                certbotPassword = photosite.ssl!!.keyStorePassword!!,
                forceUpdate = forceUpdate,
                expiryDate = expiryDate,
                gracePeriod = 1
            )
            if (newExpiryDate.isAfter(expiryDate)) {
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
        photosite.reloadPageTree()
        response.sendRedirect("/")
    }

    private fun createThumbnails(rootFolder: File) {
        log.info("Creating thumbnails for folder: $rootFolder")
        rootFolder.listFiles { file: File ->
            file.isFile() && file.getName().lowercase(Locale.getDefault()).endsWith(".jpg")
        }?.forEach { imageFile ->
            imageService.getThumbnail(ImageFile(imageFile))
        }

        // recurse into sub folders
        rootFolder
            .listFiles { obj: File -> obj.isDirectory() }
            ?.forEach { rf -> createThumbnails(rf) }
    }
}
