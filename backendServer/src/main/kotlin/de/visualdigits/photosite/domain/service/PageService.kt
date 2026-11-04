package de.visualdigits.photosite.domain.service

import de.visualdigits.photosite.domain.data.model.common.Language
import de.visualdigits.photosite.domain.data.model.page.Page
import de.visualdigits.photosite.domain.data.model.page.Page.Companion.mainNaviHtml
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import org.springframework.ui.Model

@Service
class PageService(
    private val photositeService: PhotositeService,
    private val imageService: ImageService,
) {

    @PostConstruct
    fun initialize() {
    }

    fun renderPage(
        lang: Language,
        requestUri: String,
        model: Model
    ): String? {
        val photosite = photositeService.getSecrets()
        val locale = Language(lang.language)
        val currentPage = photosite.pageMap[requestUri] ?: return null

        val currentPagePath = currentPage.path
        model.addAttribute("language", locale.language)
        model.addAttribute("theme", photosite.theme)
        model.addAttribute("siteUrl", photosite.siteUrl)
        model.addAttribute("title", photosite.siteTitle)
        model.addAttribute(
            "naviMain",
            mainNaviHtml(
                page = photosite.mainTree,
                navigationEntry = photosite.naviMain ?: error("No main navi"),
                locale = locale,
                currentPage = currentPage,
                theme = photosite.theme
            )
        )
        model.addAttribute(
            "naviSub",
            photosite.subTrees
                .mapIndexed { index, page ->
                    Page.subNaviHtml(
                        page = page,
                        locale = locale,
                        currentPage = currentPage,
                        theme = photosite.theme,
                        level = 0,
                        rolePrefix = "sub-navigation_${index + 1}"
                    )
                }.joinToString("")
        )
        model.addAttribute(
            "naviStatic", Page.subNaviHtml(
                page = photosite.staticTree,
                locale = locale,
                currentPage = currentPage,
                theme = photosite.theme,
                level = 0,
                rolePrefix = "static-navigation"
            )
        )

        val languageSelector = photosite.languages.joinToString("") { l ->
            val isCurrentLocale = l == locale
            val current = if (isCurrentLocale) " class=\"current\"" else ""
            val path = currentPage.path(l)
            "<li><a${current} href=\"/pagetree/$path?lang=${l.language}\">${l.language}</a></li>"
        }

        model.addAttribute("languageSelector", "<ol>$languageSelector</ol>")
        photosite.pageMap[currentPagePath]?.let { page ->
            val keywords = page.content.keywords.toMutableList()
            val path = page.path(locale)
            model.addAttribute("breadcrumb", path)
            model.addAttribute("metaKeywords", keywords.joinToString(", "))
            model.addAttribute("metaDescription", keywords.joinToString(" "))
            val pluginConfig = photosite.pluginsMap[page.content.contentType]
            model.addAttribute("head", pluginConfig?.getHead(photosite.theme))
            model.addAttribute("content", pluginConfig?.renderHtml(page, locale, imageService))
        }

        return "pagetemplate"
    }
}
