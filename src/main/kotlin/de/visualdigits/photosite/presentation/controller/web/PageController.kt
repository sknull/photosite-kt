package de.visualdigits.photosite.presentation.controller.web

import de.visualdigits.photosite.domain.data.model.common.Language
import de.visualdigits.photosite.domain.service.PageService
import de.visualdigits.photosite.domain.service.ResourceService
import de.visualdigits.photosite.presentation.util.HtmlUtil.getRequestUri
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller("PageController")
class PageController(
    private val pageService: PageService,
    private val resourceService: ResourceService,
) {

    @GetMapping(value = ["/**"], produces = ["application/xhtml+xml"])
    fun dispatch(
        @RequestParam(name = "lang", required = false, defaultValue = "de") lang: Language,
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
    ): String? {
        val requestUri = request.getRequestUri()
        return if (requestUri.startsWith("/resources") || requestUri.startsWith("/.well-known")) {
            resourceService.getResource(request, response)
            null
        } else if (requestUri.startsWith("/pagetree/")) {
            pageService.renderPage(lang, requestUri.removePrefix("/pagetree/"), model)
        } else {
            pageService.renderPage(lang, requestUri.removePrefix("/"), model)
        }
    }
}
