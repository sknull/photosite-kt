package de.visualdigits.photosite.controller.internal

import de.visualdigits.photosite.service.MaintenanceService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class MaintenanceController(
    private val maintenanceService: MaintenanceService
) {

    @GetMapping(value = ["/maintenance/checkCerts"])
    fun checkCerts(
        @RequestParam(value = "forceUpdate", required = false, defaultValue = "false") forceUpdate: Boolean,
        @RequestParam(value = "gracePeriod", required = false, defaultValue = "7") gracePeriod: Long,
        response: HttpServletResponse
    ) {
        maintenanceService.checkCerts(forceUpdate, response)
    }

    @GetMapping(value = ["/maintenance/createThumbnails"])
    fun createThumbnails(response: HttpServletResponse) {
        maintenanceService.createThumbnails(response)
    }

    @GetMapping(value = ["/maintenance/reloadPageTree"])
    fun reloadPageTree(response: HttpServletResponse) {
        maintenanceService.reloadPageTree(response)
    }
}