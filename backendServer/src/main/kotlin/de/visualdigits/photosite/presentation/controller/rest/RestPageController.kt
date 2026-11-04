package de.visualdigits.photosite.presentation.controller.rest

import de.visualdigits.photosite.domain.data.repository.DatabasePageRepository
import de.visualdigits.photosite.domain.service.PhotositeService
import de.visualdigits.photosite.presentation.model.PageDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class RestPageController(
    private val photositeService: PhotositeService,
    private val databasePageRepository: DatabasePageRepository
) {

    @GetMapping(value = ["/api/v1/navigation/static"], produces = ["application/json"])
    fun getNavigationStatic(): PageDto {
        return photositeService.getSecrets().staticTreeDto
    }

    @GetMapping(value = ["/api/v1/navigation/main"], produces = ["application/json"])
    fun getNavigationMain(): PageDto {
        return photositeService.getSecrets().mainTreeDto
    }

    @GetMapping(value = ["/api/v1/page"], produces = ["application/json"])
    fun getPage(
        @RequestParam("path") path: String
    ): PageDto? {
        return databasePageRepository.getPageDto(path)
    }
}
