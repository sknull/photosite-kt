package de.visualdigits.photosite.presentation.controller.rest

import de.visualdigits.photosite.domain.data.repository.DatabasePageRepository
import de.visualdigits.photosite.presentation.model.PageDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class RestPageController(
    private val databasePageRepository: DatabasePageRepository
) {

    @GetMapping(value = ["/api/v1/pages"], produces = ["application/json"])
    fun getAllPages(): PageDto? {
        return databasePageRepository.getPageDtoTree()
    }
}
