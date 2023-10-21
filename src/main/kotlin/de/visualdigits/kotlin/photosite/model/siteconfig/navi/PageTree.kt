package de.visualdigits.kotlin.photosite.model.siteconfig.navi

import de.visualdigits.kotlin.photosite.model.page.Page
import de.visualdigits.kotlin.photosite.model.page.PageByDateComparator
import org.slf4j.LoggerFactory
import java.io.File
import java.time.OffsetDateTime
import java.util.*
import kotlin.math.min

class PageTree(
    pageDirectory: File? = null,
    nameFilter: ((s: String) -> Boolean)? = null,
    dump: Boolean = false
) {
    private val log = LoggerFactory.getLogger(PageTree::class.java)

    val pages: MutableMap<String, Page> = mutableMapOf()

    var rootPage: Page? = readTree(pageDirectory, nameFilter, dump)

    fun clear() {
        pages.clear()
    }

    fun add(page: Page) {
        pages[page.path!!] = page
    }

    fun remove(path: String): Page? {
        val page = getPage(path)
        if (page != null) {
            page.childs.forEach { c -> c.parent = null }
            pages.remove(path)
        }
        return page
    }

    fun getPage(path: String?): Page? {
        var p = path
        p = if (p?.isEmpty() == true) {
            "pagetree"
        } else {
            val lpath = p
                ?.split("/")
                ?.dropLastWhile { it.isEmpty() }
                ?.toMutableList()
                ?: mutableListOf()
            if ("pagetree" != lpath.first()) {
                lpath.add(0, "pagetree")
            }
            lpath.joinToString("/")
        }
        return pages[p]
    }

    fun lastModified(): OffsetDateTime {
        return lastModified(rootPage?.path)
    }

    fun getLastModifiedPages(count: Int): List<Page> {
        return getLastModifiedPages(rootPage?.path, count)
    }

    protected fun lastModified(path: String?): OffsetDateTime {
        val pages = getLastModifiedPages(path, 1)
        return pages[0].lastModifiedTimestamp
    }

    fun getLastModifiedPages(path: String?, count: Int): List<Page> {
        var pages = path?.let { getPages(getPage(it)) }
        pages = pages?.sortedWith(PageByDateComparator())
        if (count > 0) {
            pages = pages?.subList(0, min(count.toDouble(), pages.size.toDouble()).toInt())
        }
        return pages?:listOf()
    }

    private fun getPages(page: Page?, pages: MutableList<Page> = mutableListOf()): List<Page> {
        page?.childs?.forEach { c -> getPages(c, pages) }
        page?.let { if (!pages.contains(it)) pages.add(it) }
        return pages
    }

    private fun readTree(
        pageDirectory: File?,
        nameFilter: ((s: String) -> Boolean)? = null,
        dump: Boolean = false
    ): Page? {
        return readTree(pageDirectory, LinkedList(), nameFilter, dump)
    }

    private fun readTree(
        pageDirectory: File?,
        path: LinkedList<String>,
        nameFilter: ((s: String) -> Boolean)? = null,
        dump: Boolean
    ): Page? {
        val name = pageDirectory?.name
        name?.let { path.add(it) }
        val descriptorFile = File(pageDirectory, "page.xml")
        var page: Page? = null
        if (nameFilter == null || name?.let { n -> nameFilter(n) } == true) {
            if (descriptorFile.exists()) {
                page = Page.load(descriptorFile)
            } else if ("thumbs" != name) {
                page = Page()
                page.name = name
                pageDirectory?.let { pd -> page.loadExternalContent(pd) }
            }
        }
        if (page != null) {
            val pagePath: String = path.joinToString("/")
            page.path = pagePath
            add(page)
            if (dump) {
                log.info("### added page '$pagePath'")
            }
            pageDirectory
                ?.listFiles { obj: File -> obj.isDirectory() }
                ?.forEach { directory ->
                    val child = readTree(directory, path, nameFilter, dump)
                    path.removeLast()
                    if (child != null) {
                        child.parent = page
                        page.addChild(child)
                    }
                }
        }
        return page
    }
}
