package de.visualdigits.kotlin.photosite.controller

import de.visualdigits.kotlin.photosite.model.siteconfig.SiteConfigHolder
import de.visualdigits.kotlin.photosite.model.siteconfig.navi.PageTree
import de.visualdigits.kotlin.photosite.util.PageHelper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam


@Controller("PageController")
class PageController : AbstractBaseController() {

    @Autowired
    private lateinit var pageHelper: PageHelper

    @GetMapping(value = ["/**"], produces = ["application/xhtml+xml"])
    fun page(
        @RequestParam(name = "lang", required = false) lang: String?,
        model: Model,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): String? {
        val currentPage: String = pageHelper.determinePage(getRequestUri(request))
        val requestUri = getRequestUri(request)
        val siteConfig: SiteConfigHolder = pageTreeHolder.siteConfig
        return if (resourceFileExists(siteConfig, "", requestUri)) {
            getResource(siteConfig, response, "", requestUri)
            null
        } else {
            val site = pageTreeHolder.site!!
            val pageTree = pageTreeHolder.pageTree!!
            val language = lang?:site.languageDefault!!
            val theme = site.theme!!
            model.addAttribute("theme", theme)
            model.addAttribute("siteUrl", pageTreeHolder.siteUrl)
            model.addAttribute("language", language)
            model.addAttribute("title", site.siteTitle)
            model.addAttribute("naviMain", pageHelper.createMainNavigation(siteConfig, pageTree, currentPage, language))
            model.addAttribute("naviSub", pageHelper.createSubNavigation(siteConfig, pageTree, language))
            val fullPageTreeStatic = PageTree(
                pageFactory,
                pageTreeHolder.pageDirectory,
                { name -> "pagetree" == name || name.startsWith("-") },
                false
            )
            val pageTreeStatic = pageHelper.createPagetreeStatic(pageFactory, fullPageTreeStatic)
            val naviStatic = pageHelper.createStaticNavigation(siteConfig, pageTreeStatic, language)
            model.addAttribute("naviStatic", naviStatic)
            pageHelper.createContent(siteConfig, currentPage, model, language, pageTree, fullPageTreeStatic)
            "pagetemplate"
        }
    }
}

