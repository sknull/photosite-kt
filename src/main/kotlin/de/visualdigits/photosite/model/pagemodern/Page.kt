package de.visualdigits.photosite.model.pagemodern

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import java.io.File
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.collections.maxOfOrNull


class Page(
    val icon: Any? = null,
    @JsonAlias("tocname", "tocName") val tocName: String? = null,
    var content: Content = Content(),
    val i18n: List<Lang> = listOf()
) {
    var level: Int = 0
    var name: String = "/"

    var directory: File? = null
    var files: Array<File> = arrayOf()

    var parent: Page? = null
    var children: MutableList<Page> = mutableListOf()

    companion object {

        private val xmlMapper = XmlMapper.builder()
            .addModule(kotlinModule())
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build()

        fun readValue(directory: File, level: Int = 0): Page {
        println("${"  ".repeat(level)}${directory.canonicalPath}")
            val files = directory.listFiles()?:arrayOf()
            val descriptorFile = File(directory, "page.xml")
            val page = if (descriptorFile.exists()) {
                xmlMapper.readValue(descriptorFile, Page::class.java)
            } else {
                Page()
            }
            page.level = level
            page.name = directory.name
            page.directory = directory
            page.files = files

            page.loadContent()

            page.children = files
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
    }

    override fun toString(): String {
        return "${"  ".repeat(level)}$name [${path()}]\n${children.joinToString("") { it.toString() }}"
    }

    fun clone(childrenFilter: ((p: Page) -> Boolean)? = null ): Page {
        val clone = Page(
            icon = icon,
            tocName = tocName,
            content = content,
            i18n = i18n
        )
        clone.level = level
        clone.name = name
        clone.directory = directory
        clone.files = files
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

    fun loadContent() {
        val mdFile = File(directory, "page.md")
        if (mdFile.exists()) {
            content.contentType = ContentType.Markdown
            content.mdContent = mdFile.readText()
        }

        val htmlFile = File(directory, "page.html")
        if (htmlFile.exists()) {
            content.contentType = ContentType.Html
            content.htmlContent = htmlFile.readText()
        }

        content.loadImages(files.filter { f -> f.isFile && f.extension == "jpg" })
    }

    fun path(): String = rootLine().joinToString("/") { p -> p.name }

    fun rootLine(rootLine: MutableList<Page> = mutableListOf()): List<Page> {
        rootLine.addFirst(this)
        parent?.also { p -> p.rootLine(rootLine) }

        return rootLine
    }
}
