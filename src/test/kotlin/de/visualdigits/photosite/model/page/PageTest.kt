package de.visualdigits.photosite.model.page

import de.visualdigits.photosite.domain.data.model.common.Language
import de.visualdigits.photosite.domain.data.model.page.Page
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Paths
import java.util.Locale

@Disabled
class PageTest {

    @Test
    fun testLocale() {
        println(Locale.GERMAN.language)
    }

    @Test
    fun testTeaserHtml() {
        val page = Page.readValue(File("C:\\Users\\sknull\\.photosite\\resources\\pagetree\\Fotos\\Unterwegs\\Deutschland\\Hamburg\\Stadtteile\\Baakenhöft\\Zeitreise"))
        println(page.content.teaser?.getHtml(Language("de")))
    }

    @Test
    fun testConvertDescriptor() {
        val rootDirectory = "C:\\Users\\sknull\\.photosite\\resources\\pagetree"
        val page = Page.readValue(File(rootDirectory))
        visitPage(rootDirectory.substringBeforeLast("\\"), page)
    }

    private fun visitPage(rootDirectory: String, page: Page, indent: String = "") {
        println(Paths.get(rootDirectory,  page.rootLine().joinToString("/") { p -> p.path }))
        page.children.forEach { child -> visitPage(rootDirectory, child, "  $indent") }
    }
}
