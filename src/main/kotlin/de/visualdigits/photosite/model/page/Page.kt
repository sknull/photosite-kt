package de.visualdigits.photosite.model.page

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import com.fasterxml.jackson.module.kotlin.kotlinModule
import de.visualdigits.photosite.model.common.Translation
import de.visualdigits.photosite.model.navi.NaviName
import de.visualdigits.photosite.model.page.content.Content
import org.apache.commons.text.StringEscapeUtils
import org.slf4j.LoggerFactory
import java.io.File
import java.time.OffsetDateTime
import java.util.Locale

@JsonIgnoreProperties(
    "level",
    "parent",
    "children",
    "translationsMap"
)
class Page(
    var icon: String? = null,
    val tocName: String? = null,
    var content: Content = Content(),
    val translations: List<Translation> = listOf()
) {

    var level: Int = 0
    var path: String = "/"

    var ariaName: String = ""

    var parent: Page? = null
    var children: MutableList<Page> = mutableListOf()

    var lastModified: OffsetDateTime = OffsetDateTime.MIN

    val translationsMap: Map<Locale, Translation> = translations.associateBy { t -> t.lang!! }

    companion object {

        private val log = LoggerFactory.getLogger(Page::class.java)

        private val jsonMapper = jacksonMapperBuilder()
            .addModule(kotlinModule())
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .build()
            .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)

        fun readValue(directory: File, level: Int = 0, ariaName: String = "navigation"): Page {
            log.info("Initializing page '${"  ".repeat(level)}${directory.canonicalPath}'")

            val descriptorFile = File(directory, "page.json")
            val page = if (descriptorFile.exists()) {
                jsonMapper.readValue(descriptorFile, Page::class.java)
            } else {
                Page()
            }
            page.level = level
            page.ariaName = ariaName
            page.path = directory.name
            page.content.descriptorFile = descriptorFile
            page.content.directory = directory
            page.content.files = directory.listFiles()?:arrayOf()

            page.content.loadContent()
            page.content.loadImages()
            page.content.sortImages()

            page.children = page.content.files
                .filter { f -> f.isDirectory }
                .mapIndexed { index, d ->
                    val c = readValue(d, level + 1, "${ariaName}_${index + 1}")
                    c.parent = page
                    c
                }
                .sortedBy { c -> c.path() }
                .toMutableList()
            if (page.children.isNotEmpty()) {
                page.icon = "folder"
            }

            page.calculateLastModified()

            return page
        }

        fun mainNaviHtml(
            page: Page,
            naviName: NaviName,
            locale: Locale,
            currentPage: Page,
            theme: String
        ): String {
            val name = naviName.label?.translationsMap[locale]?.name
            val html = StringBuilder()
            val childAriaName = if (page.children.isNotEmpty()) " aria-activedescendant=\"${page.children.firstOrNull()?.ariaName}-item\"" else ""

            html
                .append("                        <span class=\"sidebar-title\">$name</span>\n")
                .append("                        <ul id=\"main-navigation-box\" role=\"navigation\" itemscope itemtype=\"https://schema.org/BreadcrumbList\" class=\"toplevel\"$childAriaName>\n")

            appendChildPages(
                theme = theme,
                currentPage = currentPage,
                page = page,
                locale = locale,
                indent = "                            ",
                html = html
            )

            html.append("                        </ul>\n")

            return html.toString()
        }

        fun subNaviHtml(
            naviName: NaviName,
            locale: Locale,
            currentPage: Page,
            pages: List<Page>,
            theme: String,
            level: Int? = null,
            rolePrefix: String
        ): String {
            val name = naviName.label?.translationsMap[locale]?.name
            val html = StringBuilder()

            html
                .append("\n          <div id=\"$rolePrefix-wrapper\"> <!-- $name - start -->\n")
                .append("              <span class=\"sidebar-title\">$name</span>\n")
                .append("              <ul id=\"$rolePrefix-box\" role=\"navigation\" itemscope itemtype=\"https://schema.org/BreadcrumbList\" class=\"toplevel\" aria-activedescendant=\"$rolePrefix-1-item\">\n")

            val numberOfPages = pages.size
            pages.forEachIndexed { index, page ->
                val clazz = determineStyleClass(page, currentPage)
                val html1 = StringBuilder()
                    .append("                  <li id=\"$rolePrefix-${index + 1}-item\" class=\"$clazz\" itemprop=\"itemListElement\" itemscope itemtype=\"https://schema.org/ListItem\" aria-posinset=\"${index + 1}\" aria-setsize=\"$numberOfPages\">\n")
                    .append(pageLink(page, theme, locale, "                      ", level))
                    .append("                      <meta itemprop=\"position\" content=\"${index + 1}\"/>")
                    .append("                  </li>\n")
                html.append(html1)
            }

            html
                .append("              </ul>\n")
                .append("          </div> <!-- $name - end -->\n      ")
            return html.toString()
        }

        private fun pageLink(
            page: Page,
            theme: String,
            locale: Locale,
            indent: String? = "",
            level: Int? = null,
        ): String {
            val html = StringBuilder()
            html.append("$indent<a href=\"/${StringEscapeUtils.escapeHtml4(page.path(locale))}?lang=$locale&\" itemprop=\"item\" style=\"padding-left: ${10 + (level?:page.level) * 10}px;\">")
                .append("<div class=\"nav-item\">")
            page.icon?.let { i -> html.append("<div class=\"nav-icon\" itemprop=\"image\"><img src=\"/resources/themes/$theme/images/icons/$i.png\" alt=\"\"/></div>") }
            html.append("<div class=\"nav-text\" itemprop=\"name\">${page.translationsMap[locale]?.name?:page.path}</div>")
                .append("</div>")
                .append("</a>\n")

            return html.toString()
        }

        private fun appendChildPages(
            theme: String,
            currentPage: Page,
            page: Page,
            locale: Locale,
            indent: String,
            html: StringBuilder,
            children: List<Page> = page.children
        ) {
            val numberOfChildren = children.size
            children.forEachIndexed { index, child ->
                val clazz = determineStyleClass(child, currentPage)
                val childAriaName1 = if (child.children.isNotEmpty()) " aria-activedescendant=\"${child.ariaName}-box\"" else ""
                val html1 = StringBuilder("$indent<li id=\"${child.ariaName}-item\" class=\"$clazz\" itemprop=\"itemListElement\" itemscope itemtype=\"https://schema.org/ListItem\" aria-posinset=\"${index + 1}\" aria-setsize=\"$numberOfChildren\"$childAriaName1>\n")
                    .append(pageLink(child, theme, locale, "$indent    "))

                if (child.children.isNotEmpty()) {
                    val subFolders = child.children.filter { c -> c.children.isNotEmpty() }
                    val subPages = child.children.filter { c -> c.children.isEmpty() }
                    val childAriaName2 = if (subFolders.isNotEmpty()) {
                        " aria-activedescendant=\"${subFolders.first().ariaName}-item\""
                    } else if (subPages.isNotEmpty()) {
                        " aria-activedescendant=\"${subPages.first().ariaName}-item\""
                    } else {
                        ""
                    }
                    html1.append("$indent    <ul id=\"${child.ariaName}-box\" itemscope itemprop=\"folder\" role=\"navigation\"$childAriaName2>\n")
                    appendChildPages(theme, currentPage, child, locale, "$indent        ", html1, subFolders)
                    appendChildPages(theme, currentPage, child, locale, "$indent        ", html1, subPages)
                    html1.append("$indent    </ul>\n")
                }
                html1.append("$indent    <meta itemprop=\"position\" content=\"${index + 1}\"/>\n")
                html1.append("$indent</li>\n")
                html.append(html1)
            }
        }

        private fun determineStyleClass(page: Page, currentPage: Page): String {
            val pagePath: String = page.path()
            val currentPagePath = currentPage.path()
            val isFolder: Boolean = page.children.isNotEmpty()
            val isCurrent = pagePath == currentPagePath
            val inCurrentPath = currentPagePath.contains(pagePath)
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

    override fun toString(): String {
        return "${"  ".repeat(level)}$ariaName:$path [${path()}]\n${children.joinToString("") { it.toString() }}"
    }

    fun clone(childrenFilter: ((p: Page) -> Boolean)? = null ): Page {
        val clone = Page(
            icon = icon,
            tocName = tocName,
            content = content,
            translations = translations,
        )
        clone.level = level
        clone.ariaName = ariaName
        clone.path = path
        val clonedChildren = children
            .map { c ->
                val cc = c.clone()
                cc.parent = clone
                cc
            }
        clone.children = (childrenFilter
            ?.let { cf -> clonedChildren.filter(cf) }
            ?: clonedChildren).toMutableList()

        return clone
    }

    fun page(path: String, locale: Locale? = null): Page? {
        return createPageMap(locale)[path]
    }

    fun allPages(pages: MutableList<Page> = mutableListOf(), filter: ((p: Page) -> Boolean)? = null): List<Page> {
        if (filter == null || filter(this)) pages.add(this)
        children.forEach { c -> c.allPages(pages, filter) }

        return pages
    }

    private fun createPageMap(locale: Locale? = null, pageMap: MutableMap<String, Page> = mutableMapOf()): Map<String, Page> {
        pageMap[path(locale)] = this
        children.forEach { c ->
            c.createPageMap(locale, pageMap)
        }

        return pageMap
    }

    fun calculateLastModified() {
        val allPages = allPages()
        lastModified = allPages.maxOf { p -> p.content.lastModified }
    }

    fun lastModifiedPages(count: Int? = null, filter: ((p: Page) -> Boolean)? = null): List<Page> {
        return allPages(filter = filter)
            .sortedByDescending { p -> p.content.lastModified }
            .let { l ->
                count
                    ?.let { c -> l.take(c) }
                    ?: l
            }
    }

    fun path(locale: Locale? = null): String = rootLine().drop(1).joinToString("/") { p ->
        locale?.let { l -> p.translationsMap[l]?.name }?:p.path
    }

    fun rootLine(rootLine: MutableList<Page> = mutableListOf()): List<Page> {
        rootLine.addFirst(this)
        parent?.also { p -> p.rootLine(rootLine) }

        return rootLine
    }
}
