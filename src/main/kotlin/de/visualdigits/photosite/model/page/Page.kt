package de.visualdigits.photosite.model.page

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import com.fasterxml.jackson.module.kotlin.kotlinModule
import de.visualdigits.photosite.model.siteconfig.navi.NaviName
import org.apache.commons.text.StringEscapeUtils
import org.slf4j.LoggerFactory
import java.io.File
import java.util.Locale

@JsonIgnoreProperties(
    "level",
    "parent",
    "children",
    "translationsMap"
)
class Page(
    val icon: Any? = null,
    @JsonAlias("tocname", "tocName") val tocName: String? = null,
    var content: Content = Content(),
    @JsonAlias("i18n", "translations") val translations: List<Translation> = listOf()
) {

    var level: Int = 0
    var name: String = "/"

    var parent: Page? = null
    var children: MutableList<Page> = mutableListOf()

    val translationsMap: Map<Locale, Translation> = translations.associateBy { t -> t.lang!! }

    companion object {

        private val log = LoggerFactory.getLogger(Page::class.java)

        private val jsonMapper = jacksonMapperBuilder()
            .addModule(kotlinModule())
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .build()
            .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)

        fun readValue(directory: File): Page {
            return readValue(directory, 0)
        }

        private fun readValue(directory: File, level: Int): Page {
            log.info("Initializing page '${"  ".repeat(level)}${directory.canonicalPath}'")

            val descriptorFile = File(directory, "page.json")
            val page = if (descriptorFile.exists()) {
                jsonMapper.readValue(descriptorFile, Page::class.java)
            } else {
                Page()
            }
            page.level = level
            page.name = directory.name
            page.content.descriptorFile = descriptorFile
            page.content.directory = directory
            page.content.files = directory.listFiles()?:arrayOf()

            page.content.loadContent()
            page.content.loadImages()
            page.content.sortImages()

            page.children = page.content.files
                .filter { f -> f.isDirectory }
                .map { d ->
                    val c = readValue(d, level + 1)
                    c.parent = page
                    c
                }
                .sortedBy { c -> c.name }
                .toMutableList()

            return page
        }

        fun subNaviHtml(
            naviName: NaviName,
            language: Locale,
            currentPage: Page,
            pages: List<Page>,
            theme: String,
            level: Int? = null
        ): String {
            val name = naviName.label?.translationsMap[language]?.name
            val html = StringBuilder()
            html
                .append("          <div class=\"sub-navigation\"> <!-- ")
                .append(name)
                .append(" -->\n")
                .append("              <span class=\"sidebar-title\">")
                .append(name)
                .append("</span>\n")
                .append("              <ul class=\"toplevel\" role=\"menubar\">\n")

            pages.forEach { page ->
                val clazz = determineStyleClass(page, currentPage)
                val html1 = StringBuilder("                  <li class=\"$clazz\">\n")
                    .append(page.pageLink(theme, language, "", level))
                    .append("                  </li>\n")
                html.append(html1)
            }

            html
                .append("              </ul>\n")
                .append("          </div> <!-- ")
                .append(name)
                .append(" -->\n")
            return html.toString()
        }

        private fun appendChildPages(
            theme: String,
            currentPage: Page,
            page: Page,
            language: Locale,
            indent: String,
            html: StringBuilder,
            predicate: (p: Page) -> Boolean
        ) {
            val children: List<Page> = page.children.filter(predicate).sortedBy { p -> p.path() }
            children.forEach { child ->
                val clazz = determineStyleClass(child, currentPage)
                val html1 = StringBuilder("$indent<li class=\"$clazz\">\n")
                    .append(child.pageLink(theme, language, indent))
                    .append(indent)
                    .append("  <ul>\n")
                appendChildPages(theme, currentPage, child, language, "$indent    ", html1) { p: Page -> p.children.isNotEmpty() }
                appendChildPages(theme, currentPage, child, language, "$indent    ", html1) { p: Page -> p.children.isEmpty() }
                html1.append(indent)
                    .append("  </ul>\n")
                    .append(indent)
                    .append("</li>\n")
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
        return "${"  ".repeat(level)}$name [${path()}]\n${children.joinToString("") { it.toString() }}"
    }

    fun mainNaviHtml(
        naviName: NaviName,
        language: Locale,
        currentPage: Page,
        theme: String
    ): String {
        val name = naviName.label?.translationsMap[language]?.name
        val html = StringBuilder()
        html.append("          <nav role=\"navigation\" itemscope=\"itemscope\" itemtype=\"http://schema.org/SiteNavigationElement\"> <!-- ")
            .append(name)
            .append(" -->\n")
            .append("              <span class=\"sidebar-title\">")
            .append(name)
            .append("</span>\n")
            .append("              <ul class=\"toplevel\" role=\"menubar\">\n")

        appendChildPages(theme, currentPage, this, language, "                ", html) { p -> true }

        html.append("              </ul>\n")
            .append("          </nav> <!-- ")
            .append(name)
            .append(" -->\n")

        return html.toString()
    }

    fun pageLink(
        theme: String,
        language: Locale,
        indent: String? = "",
        level: Int? = null,
    ): String {
        val html = StringBuilder()
        html.append(indent)
            .append("  <a href=\"/")
            .append(StringEscapeUtils.escapeHtml4(path()))
            .append("?lang=")
            .append(language)
            .append("&")
            .append("\" itemprop=\"url\" style=\"padding-left: ")
            .append(10 + (level?:this.level) * 10)
            .append("px;\">\n")
            .append(indent)
            .append("    <div class=\"nav-item\"")
            .append(" itemprop=\"name\">")
        icon?.let { i ->
            html.append("<div class=\"nav-icon\"><img src=\"/resources/themes/")
                .append(theme)
                .append("/images/icons/")
                .append(i)
                .append(".png\"/></div>")
        }
        html.append("<div class=\"nav-text\">")
            .append(translationsMap[language]?.name?:name)
            .append("</div>")
        html.append("</div>\n")
        html.append(indent)
            .append("  </a>\n")

        return html.toString()
    }

    fun clone(childrenFilter: ((p: Page) -> Boolean)? = null ): Page {
        val clone = Page(
            icon = icon,
            tocName = tocName,
            content = content,
            translations = translations
        )
        clone.level = level
        clone.name = name
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

    fun page(path: String): Page? {
        return createPageMap()[path]
    }

    fun allPages(allPages: MutableList<Page> = mutableListOf(), filter: ((p: Page) -> Boolean)? = null ): List<Page> {
        if (filter?.let { f -> f(this) } == true) allPages.add(this)
        children.forEach { c -> c.allPages(allPages, filter) }

        return allPages
    }

    private fun createPageMap(pageMap: MutableMap<String, Page> = mutableMapOf()): Map<String, Page> {
        pageMap[path()] = this
        children.forEach { c ->
            c.createPageMap(pageMap)
        }

        return pageMap
    }

    fun lastModifiedPages(count: Int? = null): List<Page> {
        return allPages { p -> p.children.isEmpty() }
            .sortedByDescending { p -> p.content.lastModified }
            .let { l ->
                count
                    ?.let { c -> l.take(c) }
                    ?: l
            }
    }

    fun path(): String = rootLine().drop(1).joinToString("/") { p -> p.name }

    fun rootLine(rootLine: MutableList<Page> = mutableListOf()): List<Page> {
        rootLine.addFirst(this)
        parent?.also { p -> p.rootLine(rootLine) }

        return rootLine
    }
}
