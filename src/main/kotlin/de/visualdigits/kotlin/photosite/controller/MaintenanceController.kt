package de.visualdigits.kotlin.photosite.controller

import de.visualdigits.kotlin.photosite.Application
import de.visualdigits.kotlin.photosite.model.common.ImageFile
import de.visualdigits.kotlin.photosite.util.ImageHelper
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.File
import java.nio.file.Paths
import java.time.LocalDateTime
import java.util.*

@RestController
class MaintenanceController : AbstractBaseController() {

    private val log = LoggerFactory.getLogger(MaintenanceController::class.java)

    @GetMapping(value = ["/maintenance/checkCerts"])
    fun checkCerts(
        @RequestParam(value = "forceUpdate", required = false, defaultValue = "false") forceUpdate: Boolean
    ) {
        val expiryDate: LocalDateTime = siteConfigHolder.maintainServerCertificate(forceUpdate)
        log.info("#### new certificate is expiring $expiryDate - restartig serer now")
        Application.restart("ssl")
    }

    @GetMapping(value = ["/maintenance/createThumbnails"])
    fun createThumbnails(httpResponse: HttpServletResponse) {
        val site = siteConfigHolder.siteConfig?.site
        site?.rootFolder
            ?.let { rf ->
                createThumbnails(Paths.get(rf, site.resourcesRoot, "pagetree").toFile())
            }
        httpResponse.sendRedirect("/")
    }

    @GetMapping(value = ["/maintenance/reloadPageTree"])
    fun reloadPageTree(httpResponse: HttpServletResponse) {
        siteConfigHolder.reloadPageTree()
        httpResponse.sendRedirect("/")
    }

    private fun createThumbnails(rootFolder: File) {
        log.info("Creating thumbnails for folder: $rootFolder")
        rootFolder.listFiles { file: File ->
            file.isFile() && file.getName().lowercase(Locale.getDefault()).endsWith(".jpg")
        }?.forEach { imageFile ->
            ImageHelper.getThumbnail(siteConfigHolder.siteConfig!!, ImageFile(imageFile))
        }

        // recurse into sub folders
        rootFolder
            .listFiles { obj: File -> obj.isDirectory() }
            ?.forEach { rf -> createThumbnails(rf) }
    }
}

