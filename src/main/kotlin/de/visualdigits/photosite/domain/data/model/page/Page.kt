package de.visualdigits.photosite.domain.data.model.page

import de.visualdigits.photosite.domain.data.model.common.KmpOffsetDateTime
import de.visualdigits.photosite.domain.data.model.common.Language
import de.visualdigits.photosite.domain.data.model.common.Translation
import de.visualdigits.photosite.domain.data.model.navi.NaviName
import de.visualdigits.photosite.domain.data.model.page.content.Content
import de.visualdigits.photosite.domain.data.model.photosite.Photosite.Companion.rootDirectory
import de.visualdigits.photosite.domain.data.repository.PageRepository
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import org.apache.commons.text.StringEscapeUtils
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Paths
import java.util.UUID

@Serializable
data class Page(
    @Transient val id: UUID? = null,
    @SerialName("icon") var icon: String? = null,
    @SerialName("tocName") val tocName: String? = null,
    @SerialName("content") var content: Content = Content(),
    @SerialName("translations" )val translations: List<Translation> = listOf(),

    @Transient var directory: File? = null,
    @Transient var path: String = "/",
    @Transient var ariaName: String = "",
) {

    @Transient var level: Int = 0

    @Transient var parentPath: String = ""

    @Transient var parent: Page? = null

    @Transient var children: MutableList<Page> = mutableListOf()

    @Transient var lastModified: KmpOffsetDateTime = KmpOffsetDateTime.MIN

    @Transient val translationsMap: Map<Language, Translation> = translations.associateBy { t -> t.lang!! }

    companion object {

        private val log = LoggerFactory.getLogger(Page::class.java)

        private val jsonMapper = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            encodeDefaults = true
        }

        fun readPagetreeFromFilesystem(pageRepository: PageRepository): Page {
            val rootPage = readValue(Paths.get(rootDirectory.canonicalPath, "resources", "pagetree").toFile())
            pageRepository.addPageTree(rootPage)

            return rootPage
        }

        fun readValue(directory: File, level: Int = 0, ariaName: String = "navigation"): Page {
            log.info("Initializing page '${"  ".repeat(level)}${directory.canonicalPath}'")

            val descriptorFile = File(directory, "page.json")
            val page = if (descriptorFile.exists()) {
                val json = descriptorFile.readText()
                jsonMapper.decodeFromString<Page>(json)
            } else {
                Page()
            }

            page.directory = directory
            page.level = level
            page.ariaName = ariaName
            page.path = directory.name
            val pageFiles = directory.listFiles()?:arrayOf()

            page.content.loadContent(directory)
            page.content.loadImages(pageFiles)

            page.children = pageFiles
                .filter { f -> f.isDirectory }
                .mapIndexed { index, d ->
                    val child = readValue(d, level + 1, "${ariaName}_${index + 1}")
                    child.parent = page
                    child
                }
                .sortedBy { c -> c.path() }
                .toMutableList()
            if (page.children.isNotEmpty()) {
                page.icon = "folder"
            }

            page.calculateLastModified()

            return page
        }

        fun readPageTreeFromDatabase(pageRepository: PageRepository): Page? {
            // read all pages from database
            val pages = pageRepository
                .getPagesEager()
                .map { page ->
                    val parentPath = page.path.substringBeforeLast("/")
                    val finalParentPath = if (page.path == parentPath) {
                        ""
                    } else {
                        parentPath
                    }
                    page.parentPath = finalParentPath
                    page.level = page.path.split("/").size - 1

                    page.directory?.also { directory -> page.content.loadContent(directory) }
                    page.content.calculateLastModified()
                    page.content.sortImages()

                    page
                }
                .sortedBy { it.path }
                .associateBy { it.path }

            // reconstruct tree
            pages.values.forEach { child ->
                pages[child.parentPath]?.also { parent ->
                    child.path = child.path.substringAfterLast("/")
                    if (parent != child) {
                        child.parent = parent
                        parent.children.add(child)
                    }
                }
            }

            return pages.values.firstOrNull()?.rootPage()
        }

        fun mainNaviHtml(
            page: Page,
            naviName: NaviName,
            locale: Language,
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
            locale: Language,
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
                    .append(pageLink("subnavi", page, theme, locale, "                      ", level))
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
            naviName: String,
            page: Page,
            theme: String,
            locale: Language,
            indent: String? = "",
            level: Int? = null,
        ): String {
            val html = StringBuilder()
            val href = StringEscapeUtils.escapeHtml4("/pagetree/${page.path(locale)}")
            val id = "$naviName${href.replace("/", "-")}"
            html.append("$indent<a itemscope itemtype=\"https://schema.org/WebPage\" itemprop=\"item\" id=\"$id\" itemid=\"$id\" href=\"$href?lang=$locale&\" style=\"padding-left: ${10 + (level?:page.level) * 10}px;\">")
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
            locale: Language,
            indent: String,
            html: StringBuilder,
            children: List<Page> = page.children
        ) {
            val numberOfChildren = children.size
            children.forEachIndexed { index, child ->
                val clazz = determineStyleClass(child, currentPage)
                val childAriaName1 = if (child.children.isNotEmpty()) " aria-activedescendant=\"${child.ariaName}-box\"" else ""
                val html1 = StringBuilder("$indent<li id=\"${child.ariaName}-item\" class=\"$clazz\" itemprop=\"itemListElement\" itemscope itemtype=\"https://schema.org/ListItem\" aria-posinset=\"${index + 1}\" aria-setsize=\"$numberOfChildren\"$childAriaName1>\n")
                    .append(pageLink("mainnavi", child, theme, locale, "$indent    "))

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
//        return "path=$path, parentPath=$parentPath"
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

    fun page(path: String, pageTree: Page, locale: Language? = null): Page {
        return createPageMap(locale)[path] ?: pageTree
    }

    fun allPages(pages: MutableList<Page> = mutableListOf(), filter: ((p: Page) -> Boolean)? = null): List<Page> {
        if (filter == null || filter(this)) pages.add(this)
        children.forEach { c -> c.allPages(pages, filter) }

        return pages
    }

    private fun createPageMap(locale: Language? = null, pageMap: MutableMap<String, Page> = mutableMapOf()): Map<String, Page> {
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

    fun path(locale: Language? = null): String = rootLine().drop(1).joinToString("/") { p ->
        locale?.let { l -> p.translationsMap[l]?.name }?:p.path
    }

    fun rootPage(): Page? {
        val rootLine = rootLine()
        return rootLine.firstOrNull()
    }

    fun rootLine(rootLine: MutableList<Page> = mutableListOf()): List<Page> {
        rootLine.addFirst(this)
        parent?.also { p -> p.rootLine(rootLine) }

        return rootLine
    }
}
