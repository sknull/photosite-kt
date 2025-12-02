package de.visualdigits.photosite.controller

import de.visualdigits.photosite.HtmlUtil.getRequestUri
import de.visualdigits.photosite.service.PageService
import de.visualdigits.photosite.service.ResourceService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.Locale


@Controller("PageController")
class PageController(
    private val pageService: PageService,
    private val resourceService: ResourceService,
) {

    @GetMapping(value = ["/**"], produces = ["application/xhtml+xml"])
    fun dispatch(
        @RequestParam(name = "lang", required = false, defaultValue = "de") lang: Locale,
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
    ): String? {
        val requestUri = request.getRequestUri()
        return if (requestUri.startsWith("/resources")) {
            resourceService.getResource(request, response)
            null
        } else if (requestUri.startsWith("/pagetree/")) {
            pageService.renderPage(lang, requestUri.removePrefix("/pagetree/"), model)
        } else {
            pageService.renderPage(lang, requestUri.removePrefix("/"), model)
        }
    }
}

