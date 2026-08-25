package de.visualdigits.photosite.presentation.controller.web

import de.visualdigits.photosite.domain.data.model.common.Language
import de.visualdigits.photosite.domain.service.PageService
import de.visualdigits.photosite.domain.service.ResourceService
import de.visualdigits.photosite.presentation.util.HtmlUtil.getRequestUri
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.text.StringEscapeUtils
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class WebPageController(
    private val pageService: PageService,
    private val resourceService: ResourceService,
) {

    @GetMapping("/")
    fun redirectRoot(
        response: HttpServletResponse
    ) {
        response.sendRedirect("/pagetree/")
    }

    @GetMapping(value = ["/pagetree/**"], produces = ["application/xhtml+xml"])
    fun dispatch(
        @RequestParam(name = "lang", required = false, defaultValue = "de") lang: Language,
        request: HttpServletRequest,
        model: Model,
    ): String? {
        val requestUri = request.getRequestUri()
        return pageService.renderPage(lang, StringEscapeUtils.unescapeHtml4(requestUri.removePrefix("/pagetree/")), model)
    }

    @GetMapping(value = ["/resources/**"], produces = ["application/xhtml+xml"])
    fun getResource(
        @RequestParam(name = "lang", required = false, defaultValue = "de") lang: Language,
        request: HttpServletRequest,
        response: HttpServletResponse,
    ) {
        resourceService.getResource(request, response)
    }
}
