package de.visualdigits.photosite.model.page

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import de.visualdigits.photosite.model.common.ImageFile
import de.visualdigits.photosite.model.common.sort.Sort
import de.visualdigits.photosite.model.common.sort.SortDir
import de.visualdigits.photosite.model.page.teaser.Teaser
import de.visualdigits.photosite.model.pagemodern.ContentType
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.time.OffsetDateTime
import java.util.Locale
import java.util.function.Consumer


class Content(
    @JacksonXmlProperty(isAttribute = true, localName = "plugin") var contentType: ContentType = ContentType.None,
    @JacksonXmlProperty(isAttribute = true) val mode: String? = null,
    @JacksonXmlProperty(isAttribute = true) val speed: Long = 0L,
    @JacksonXmlProperty(isAttribute = true) val pause: Long = 0L,
    @JacksonXmlProperty(isAttribute = true) val download: Boolean = false,
    val sort: Sort? = null,
    val teaser: Teaser? = null,
    val captions: List<Caption> = listOf(),
    @JacksonXmlProperty(localName = "itemtype") val itemType: String? = null,
    val keywords: String? = null,
    val paragraphs: List<Paragraph>? = null
) {

    private val log = LoggerFactory.getLogger(javaClass)

    lateinit var captionsMap: Map<String, Caption>
    var mdContent: String? = null
    var htmlContent: String? = null
    var images: MutableList<ImageFile> = mutableListOf()

    private var lastModifiedTimestamp: OffsetDateTime? = null

    init {
        captionsMap = captions.associate { c -> Pair(c.name!!, c) }
    }

    fun loadContent(directory: File) {
        log.debug("Loading external content for directory: {}", directory)
        val mdFile = File(directory, "page.md")
        if (mdFile.exists()) {
            contentType = ContentType.Markdown
            mdContent = readFile(mdFile)
        }
        val htmlFile = File(directory, "page.html")
        if (htmlFile.exists()) {
            contentType = ContentType.Html
            htmlContent = readFile(htmlFile)
        }
    }

    fun loadImages(directory: File) {
        if (images.isEmpty()) {
            directory
                .listFiles { file: File ->
                    file.isFile() && file.getName().lowercase(Locale.getDefault()).endsWith(".jpg")
                }?.map { image ->
                    ImageFile(image)
                }?.toMutableList()
                ?.let { images.addAll(it) }
            sortImages()
            lastModified()
        }
    }

    private fun readFile(file: File): String {
        return if (file.exists()) {
            try {
                file.readText()
            } catch (e: IOException) {
                throw IllegalStateException("Could not read file contents: ", e)
            }
        } else ""
    }

    fun lastModified(): OffsetDateTime {
        if (lastModifiedTimestamp == null) {
            lastModifiedTimestamp = images
                .maxOfOrNull { i -> i.lastModified() }
        }

        return lastModifiedTimestamp ?: OffsetDateTime.MIN
    }

    private fun sortImages() {
        val sort = sort ?: Sort(by = "name", dir = SortDir.asc)
        if (sort.by == "manual") {
            val map = mutableMapOf<String, ImageFile>()
            images.forEach(Consumer { p: ImageFile -> map[p.file.getName()] = p })
            val sortedPages: MutableList<ImageFile> = ArrayList()
            sort.orderList.forEach { p ->
                val imageFile = map[p]
                if (imageFile != null) {
                    sortedPages.add(imageFile)
                    images.remove(imageFile)
                }
            }
            images.let { sortedPages.addAll(it) }
            images.clear()
            images.addAll(sortedPages)
        } else {
            when (sort.by) {
                "name" -> images.sortBy { it.name }
                "mtime" -> images.sortBy { it.lastModified() }
            }
            if (sort.dir == SortDir.desc) {
                images.reverse()
            }
        }
    }

}
