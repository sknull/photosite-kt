package de.visualdigits.kotlin.photosite.controller

import de.visualdigits.kotlin.photosite.model.siteconfig.navi.PageTree
import de.visualdigits.kotlin.photosite.util.PageHelper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam


@Controller("PageController")
class PageController : AbstractBaseController() {

    @GetMapping(value = ["/**"], produces = ["application/xhtml+xml"])
    fun page(
        @RequestParam(name = "lang", required = false) lang: String?,
        model: Model,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): String? {
        val currentPage = PageHelper.determinePage(getRequestUri(request))
        val requestUri = getRequestUri(request)
        val siteConfig = siteConfigHolder.siteConfig!!
        return if (resourceFileExists(siteConfig, "", requestUri)) {
            getResource(siteConfig, response, "", requestUri)
            null
        } else {
            val site = siteConfigHolder.siteConfig?.site!!
            val pageTree = siteConfigHolder.pageTree!!
            val language = lang?:site.languageDefault!!
            val theme = site.theme!!
            model.addAttribute("theme", theme)
            model.addAttribute("siteUrl", siteConfigHolder.siteUrl)
            model.addAttribute("language", language)
            model.addAttribute("title", site.siteTitle)
            model.addAttribute("naviMain", PageHelper.createMainNavigation(siteConfig, pageTree, currentPage, language))
            model.addAttribute("naviSub", PageHelper.createSubNavigation(siteConfig, pageTree, language))
            val fullPageTreeStatic = PageTree(
                pageDirectory = siteConfigHolder.pageDirectory,
                nameFilter = { name -> "pagetree" == name || name.startsWith("-") },
                dump = false
            )
            val naviStatic = PageHelper.createStaticNavigation(siteConfig, fullPageTreeStatic, language)
            model.addAttribute("naviStatic", naviStatic)
            PageHelper.createContent(siteConfig, currentPage, model, language, pageTree, fullPageTreeStatic)
            "pagetemplate"
        }
    }
}

