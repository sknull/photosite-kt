package de.visualdigits.photosite.model.siteconfig.navi

import de.visualdigits.photosite.model.page.Page
import de.visualdigits.photosite.model.page.PageByDateComparator
import org.slf4j.LoggerFactory
import java.io.File
import java.time.OffsetDateTime
import kotlin.math.min

class PageTree(
    pageDirectory: File? = null,
    nameFilter: ((s: String) -> Boolean)? = null,
    dump: Boolean = false
) {
    private val log = LoggerFactory.getLogger(PageTree::class.java)

    private val pages: MutableMap<String, Page> = mutableMapOf()

    var rootPage: Page? = pageDirectory?.let { pd -> readTree(pd, nameFilter, dump) }

    private fun readTree(
        pageDirectory: File,
        nameFilter: ((s: String) -> Boolean)? = null,
        dump: Boolean = false,
        path: List<String> = listOf()
    ): Page? {
        val name = pageDirectory.name
        val descriptorFile = File(pageDirectory, "page.xml")
        var page: Page? = null
        if (nameFilter == null || nameFilter(name)) {
            if (descriptorFile.exists()) {
                page = Page.readValue(descriptorFile)
            } else if ("thumbs" != name) {
                page = Page()
                page.name = name
                page.content.loadExternalContent(pageDirectory)
            }
        }
        if (page != null) {
            val pagePath = path + name
            page.path = pagePath.joinToString("/")
            addPage(page)
            if (dump) {
                log.info("added page '$pagePath'")
            }
            pageDirectory
                .listFiles { obj: File -> obj.isDirectory() }
                ?.forEach { directory ->
                    readTree(directory, nameFilter, dump, pagePath)?.let { child ->
                        child.parent = page
                        page.addChild(child)
                    }
                }
        }
        return page
    }

    fun addPage(page: Page) {
        pages[page.path!!] = page
    }

    fun getPage(path: String): Page? {
        val p = if (path.isEmpty()) {
            "pagetree"
        } else {
            val lpath = path
                .split("/")
                .dropLastWhile { it.isEmpty() }
                .toMutableList()
            if ("pagetree" != lpath.first()) {
                lpath.add(0, "pagetree")
            }
            lpath.joinToString("/")
        }
        return pages[p]
    }

    fun lastModified(): OffsetDateTime {
        return lastModifiedPages(rootPage?.path!!, 1)
            .firstOrNull()
            ?.content?.lastModifiedTimestamp
            ?: OffsetDateTime.MIN
    }

    fun lastModifiedPages(count: Int = 0): List<Page> {
        return lastModifiedPages(rootPage?.path!!, count)
    }

    fun lastModifiedPages(path: String, count: Int = 0): List<Page> {
        var pages = path.let { getSubTree(it) }
        pages = pages.sortedWith(PageByDateComparator())
        if (count > 0) {
            pages = pages.take(min(count.toDouble(), pages.size.toDouble()).toInt())
        }
        return pages
    }

    fun getSubTree(rootPath: String, pages: MutableList<Page> = mutableListOf()): List<Page> {
        val rootPage = getPage(rootPath)
        rootPage?.childs?.forEach { c -> getSubTree(c.path!!, pages) }
        rootPage?.let { if (!pages.contains(it)) pages.add(it) }
        return pages
    }
}
