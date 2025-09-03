package de.visualdigits.photosite.model.page

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import com.fasterxml.jackson.module.kotlin.kotlinModule
import de.visualdigits.photosite.model.pagemodern.ContentType
import de.visualdigits.photosite.model.pagemodern.ImageFile
import de.visualdigits.photosite.model.pagemodern.Page
import org.junit.jupiter.api.Test
import java.io.File

class PageTest {

    private val xmlMapper = XmlMapper.builder()
        .addModule(kotlinModule())
        .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        .build()

    private val jsonMapper = jacksonMapperBuilder()
        .addModule(kotlinModule())
        .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        .enable(SerializationFeature.INDENT_OUTPUT)
        .build()
        .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)

    @Test
    fun testConvertDescriptor() {
        val tree = readPageTree(File("W:/"))

        println(tree)
    }

    private fun readPageTree(directory: File, level: Int = 0): Page {
//        println("${"  ".repeat(level)}${directory.canonicalPath}")
        val files = directory.listFiles()?:arrayOf()
        val descriptorFile = File(directory, "page.xml")
        val page = if (descriptorFile.exists()) {
            xmlMapper.readValue(descriptorFile, Page::class.java)
        } else {
            Page()
        }
        page.level = level
        page.name = directory.name
        val mdFile = File(directory, "page.md")
        if (mdFile.exists()) {
            page.content.contentType = ContentType.Markdown
            page.content.mdContent = mdFile.readText()
        }
        val htmlFile = File(directory, "page.html")
        if (htmlFile.exists()) {
            page.content.contentType = ContentType.Html
            page.content.htmlContent = htmlFile.readText()
        }
        page.content.images = files
            .filter { f -> f.isFile && f.extension == "jpg" }
            .map { f -> ImageFile(f)}
        page.children = files
            .filter { f -> f.isDirectory }
            .map { d ->
                val c = readPageTree(d, level + 1)
                c.parent = page
                c
            }

        return page
    }
}
