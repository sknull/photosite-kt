package de.visualdigits.photosite.domain.service

import de.visualdigits.photosite.domain.data.model.common.Language
import de.visualdigits.photosite.domain.data.model.page.Page
import de.visualdigits.photosite.domain.data.model.page.Page.Companion.mainNaviHtml
import de.visualdigits.photosite.domain.data.model.photosite.Photosite
import org.springframework.stereotype.Service
import org.springframework.ui.Model
import kotlin.collections.get

@Service
class PageService(
    private val photosite: Photosite,
    private val imageService: ImageService
) {

    fun renderPage(
        lang: Language,
        requestUri: String,
        model: Model
    ): String? {
        val locale = Language(lang.language)
        val currentPage = photosite.pageTree.page(requestUri, photosite.pageTree, locale)
        val currentPagePath = currentPage.path(locale)
        model.addAttribute("language", locale.language)
        model.addAttribute("theme", photosite.theme)
        model.addAttribute("siteUrl", photosite.siteUrl)
        model.addAttribute("title", photosite.siteTitle)
        model.addAttribute(
            "naviMain",
            mainNaviHtml(
                page = photosite.mainTree,
                naviName = photosite.naviMain ?: error("No main navi"),
                locale = locale,
                currentPage = currentPage,
                theme = photosite.theme
            )
        )
        model.addAttribute(
            "naviSub",
            photosite.subTrees
                .mapIndexed { index, (naviName, pages) ->
                    Page.subNaviHtml(
                        naviName,
                        locale,
                        currentPage,
                        pages,
                        photosite.theme,
                        0,
                        "sub-navigation_${index + 1}"
                    )
                }.joinToString("")
        )
        model.addAttribute(
            "naviStatic", Page.subNaviHtml(
                photosite.naviStatic ?: error("No static navi"),
                locale,
                currentPage,
                photosite.staticTree.children,
                photosite.theme,
                0,
                "static-navigation"
            )
        )

        val languageSelector = photosite.languages.joinToString("") { l ->
            val isCurrentLocale = l == locale
            val current = if (isCurrentLocale) " class=\"current\"" else ""
            val path = currentPage.path(l)
            "<li><a${current} href=\"/pagetree/$path?lang=${l.language}\">${l.language}</a></li>"
        }

        model.addAttribute("languageSelector", "<ol>$languageSelector</ol>")

        listOf(photosite.mainTree, photosite.staticTree)
            .firstNotNullOfOrNull { pageTree -> pageTree.page(currentPagePath, photosite.pageTree, locale) }
            ?.let { page ->
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
