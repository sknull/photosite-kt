package de.visualdigits.photosite.controller

import de.visualdigits.photosite.service.PageService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.Locale


@Controller("PageController")
class PageController(
    private val pageService: PageService
) {

    @GetMapping(value = ["/**"], produces = ["application/xhtml+xml"])
    fun page(
        @RequestParam(name = "lang", required = false, defaultValue = "de") lang: Locale,
        model: Model,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): String? {
        return pageService.renderPage(lang, model, request, response)
    }
}

