package de.visualdigits.photosite.controller

import de.visualdigits.photosite.Application
import de.visualdigits.photosite.model.common.ImageFile
import de.visualdigits.photosite.model.siteconfig.Photosite
import de.visualdigits.photosite.util.DomainCertificatesHelper
import de.visualdigits.photosite.util.DomainCertificatesHelper.determineExpiryDate
import de.visualdigits.photosite.util.ImageHelper
import jakarta.annotation.PostConstruct
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.File
import java.nio.file.Paths
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Locale

@RestController
class MaintenanceController(
    private val photosite: Photosite
) {

    private val log = LoggerFactory.getLogger(MaintenanceController::class.java)

    @Value("\${certbot.uri}")
    private lateinit var certbotUri: String

    @Value("\${certbot.alias}")
    private lateinit var certbotAlias: String

    @Value("\${certbot.password}")
    private lateinit var certbotPassword: String

    private lateinit var expiryDate: LocalDateTime

    @PostConstruct
    fun initialize() {
        expiryDate = determineExpiryDate(photosite, certbotAlias, certbotPassword)
    }

    @GetMapping(value = ["/maintenance/checkCerts"])
    fun checkCerts(
        @RequestParam(value = "forceUpdate", required = false, defaultValue = "false") forceUpdate: Boolean,
        @RequestParam(value = "gracePeriod", required = false, defaultValue = "7") gracePeriod: Long,
        response: HttpServletResponse
    ) {
        if (photosite.isProfileActive("prod")) {
            val newExpiryDate = DomainCertificatesHelper.maintainServerCertificate(
                photosite = photosite,
                certbotUri = certbotUri,
                certbotAlias = certbotAlias,
                certbotPassword = certbotPassword,
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

    @GetMapping(value = ["/maintenance/createThumbnails"])
    fun createThumbnails(httpResponse: HttpServletResponse) {
        createThumbnails(Paths.get(photosite.rootDirectory.canonicalPath, "resources", "pagetree").toFile())
        httpResponse.sendRedirect("/")
    }

    @GetMapping(value = ["/maintenance/reloadPageTree"])
    fun reloadPageTree(httpResponse: HttpServletResponse) {
        photosite.reloadPageTree()
        httpResponse.sendRedirect("/")
    }

    private fun createThumbnails(rootFolder: File) {
        log.info("Creating thumbnails for folder: $rootFolder")
        rootFolder.listFiles { file: File ->
            file.isFile() && file.getName().lowercase(Locale.getDefault()).endsWith(".jpg")
        }?.forEach { imageFile ->
            ImageHelper.getThumbnail(photosite, ImageFile(imageFile))
        }

        // recurse into sub folders
        rootFolder
            .listFiles { obj: File -> obj.isDirectory() }
            ?.forEach { rf -> createThumbnails(rf) }
    }
}

