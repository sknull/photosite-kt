package de.visualdigits.photosite.util

import de.visualdigits.photosite.model.common.Label
import de.visualdigits.photosite.model.common.Language
import de.visualdigits.photosite.model.page.Page
import de.visualdigits.photosite.model.page.PageByNameComparator
import de.visualdigits.photosite.model.siteconfig.Photosite
import de.visualdigits.photosite.model.siteconfig.navi.NaviName
import de.visualdigits.photosite.model.siteconfig.navi.PageTree
import org.apache.commons.text.StringEscapeUtils
import org.springframework.ui.Model
import java.util.Locale
import java.util.Objects

private const val indent = "                "

object PageHelper {

    private val PAGE_BY_NAME_COMPARATOR = PageByNameComparator()

    fun determinePage(page: String): String {
        var p = page
        if (p.startsWith("/")) {
            p = p.substring(1)
        }
        return p
    }

    fun createMainNavigation(
        photosite: Photosite,
        currentPage: String,
        language: String
    ): String {
        var pages = photosite.pageTree.rootPage?.childs?: listOf()
        pages = pages.filter { p: Page -> p.childs.isNotEmpty() }
        pages = pages.sortedWith(PAGE_BY_NAME_COMPARATOR)
        val label = photosite.naviMain?.label?.getTitle(language)
        val html = StringBuilder()
        html.append("          <nav role=\"navigation\" itemscope=\"itemscope\" itemtype=\"http://schema.org/SiteNavigationElement\">\n")
        html.append("            <span class=\"sidebar-title\">").append(label).append("</span>\n")
        html.append("\n              <ul class=\"toplevel\" role=\"menubar\">\n")
        for (child in pages) {
            appendPage(photosite, currentPage, language, html, child)
        }
        html.append("              </ul>")
        html.append("          </nav>\n")
        return html.toString()
    }

    private fun appendPage(
        photosite: Photosite,
        currentPage: String,
        language: String,
        html: StringBuilder,
        child: Page
    ) {
        val pagePath = child.path
        val clazz = determineStyleClass(child, currentPage)
        val html1 = StringBuilder("                <li class=\"$clazz\">\n")
            .append(createPageLink(photosite, child, language, 0, indent, pagePath))
            .append(indent).append("  <ul>\n")
        appendChildPages(photosite, currentPage, child, language, 0, indent, html1) { p: Page -> p.childs.isNotEmpty() }
        appendChildPages(photosite, currentPage, child, language, 0, indent, html1) { p: Page -> p.childs.isEmpty() }
        html1.append(indent).append("  </ul>\n")
            .append(indent).append("</li>\n")
        html.append(html1)
    }

    private fun appendChildPages(
        photosite: Photosite,
        currentPage: String,
        page: Page,
        language: String,
        level: Int,
        indent: String,
        html: StringBuilder,
        predicate: (p: Page) -> Boolean
    ) {
        val pages: List<Page> = page.childs.filter(predicate).sortedWith(PAGE_BY_NAME_COMPARATOR)
        for (child in pages) {
            val pagePath = child.path
            val clazz = determineStyleClass(child, currentPage)
            val html1 = StringBuilder("$indent    <li class=\"$clazz\">\n")
                .append(createPageLink(photosite, child, language, level + 1, "$indent    ", pagePath)).append(indent)
                .append("    ").append("  <ul>\n")
            appendChildPages(photosite, currentPage, child, language, level + 1, "$indent    ", html1) { p: Page -> p.childs.isNotEmpty() }
            appendChildPages(photosite, currentPage, child, language, level + 1, "$indent    ", html1) { p: Page -> p.childs.isEmpty() }
            html1.append(indent).append("    ").append("  </ul>\n").append(indent).append("    ").append("</li>\n")
            html.append(html1.toString())
        }
    }

    fun createSubNavigation(photosite: Photosite, language: String): String {
        val sb = StringBuilder()
            .append("\n")
        photosite.naviSub
            ?.forEach { ns ->
                sb.append(createSubNavigationHtml(photosite, photosite.pageTree, ns, language)) }
        sb.append("        ")
        return sb.toString()
    }

    fun createStaticNavigation(photosite: Photosite, pageTreeStatic: PageTree, language: String): String {
        val i18n: MutableList<Language> = ArrayList<Language>()
        i18n.add(Language("de", "", "S I T E L I N K S", ""))
        i18n.add(Language("en", "", "S I T E L I N K S", ""))
        val label = Label()
        label.lang = i18n
        label.initialize()
        val naviName = NaviName(
            "pagetree",
            10,
            label
        )
        return "${createSubNavigationHtml(photosite, pageTreeStatic, naviName, language)}        "
    }

    private fun createSubNavigationHtml(
        photosite: Photosite,
        pageTree: PageTree,
        naviSub: NaviName,
        language: String
    ): String {
        val rootPath = naviSub.rootFolder!!
        val pages = pageTree.lastModifiedPages(rootPath, naviSub.numberOfEntries)
        val sb = StringBuilder()
        val label = naviSub.label
        val title = label?.getTitle(language)
        sb
            .append("          <div class=\"sub-navigation\">\n")
            .append("          <span class=\"sidebar-title\">")
            .append(title)
            .append("</span>\n")
            .append("            <ul class=\"toplevel\" role=\"menubar\">\n")
        pages
            .filter { p -> "pagetree" != p.name }
            .forEach { page ->
                sb.append("              <li class=\"")
                    .append(determineStyleClass(page, ""))
                    .append("\">\n")
                    .append(createPageLink(photosite, page, language, 0, "                  ", page.path))
                    .append("              </li>\n")
            }
        sb.append("            </ul>\n")
            .append("          </div><!-- ")
            .append(rootPath)
            .append(" -->\n")
        return sb.toString()
    }

    private fun createPageLink(
        photosite: Photosite,
        page: Page,
        language: String,
        level: Int,
        indent: String?,
        pagePath: String?
    ): String {
        val html = StringBuilder()
        html.append(indent)
            .append("  <a href=\"/")
            .append(StringEscapeUtils.escapeHtml4(pagePath))
            .append("?lang=")
            .append(language)
            .append("&")
            .append("\" itemprop=\"url\" style=\"padding-left: ")
            .append(10 + level * 10)
            .append("px;\">\n")
            .append(indent)
            .append("    <div class=\"nav-item\"")
            .append(" itemprop=\"name\">")
        page.icon?.let { i ->
            html.append("<div class=\"nav-icon\"><img src=\"/resources/themes/")
                .append(photosite.theme)
                .append("/images/icons/")
                .append(i)
                .append(".png\"/></div>")
        }
        html.append("<div class=\"nav-text\">")
            .append(page.getTitle(language))
            .append("</div>")
        html.append("</div>\n")
        html.append(indent)
            .append("  </a>\n")
        return html.toString()
    }

    private fun determineStyleClass(page: Page, currentPage: String): String {
        val pagePath: String = page.normalizedPath()
        val isFolder: Boolean = page.childs.isNotEmpty()
        val isCurrent = pagePath == "pagetree/$currentPage"
        val inCurrentPath = currentPage.contains(pagePath)
        var clazz = if (isFolder) "folder" else "page"
        if (isCurrent) {
            clazz += " current"
        } else if (inCurrentPath) {
            clazz += " parent"
            if (page.parent == null) {
                clazz += " ancestor"
            }
        }
        return clazz
    }
}
