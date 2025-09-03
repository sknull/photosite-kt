package de.visualdigits.photosite.model.page

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import de.visualdigits.photosite.model.common.ImageFile
import de.visualdigits.photosite.model.common.sort.Sort
import de.visualdigits.photosite.model.common.sort.SortDir
import de.visualdigits.photosite.model.page.teaser.Teaser
import de.visualdigits.photosite.model.siteconfig.Photosite
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.Locale
import java.util.function.Consumer


class Content(
    @JacksonXmlProperty(isAttribute = true, localName = "plugin") var contentType: String? = null,
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
    var lastModifiedTimestamp: OffsetDateTime = OffsetDateTime.MIN

    init {
        captionsMap = captions.associate { c -> Pair(c.name!!, c) }
    }

    fun getHtml(page: Page, language: String): String {
//        return plugin?.let {
//            photosite.getPluginConfig(it)
//                ?.getHtml(photosite, page, language)
//                ?:""
//        }?:""
        return ""
    }

    fun loadExternalContent(directory: File) {
        log.debug("Loading external content for directory: {}", directory)
        if (mdContent == null) {
            mdContent = readFile(File(directory, "page.md"))
        }
        if (htmlContent == null) {
            htmlContent = readFile(File(directory, "page.html"))
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
            determineLastModifiedTimestamp()
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

    fun determineLastModifiedTimestamp() {
        lastModifiedTimestamp = OffsetDateTime.MIN
        images.forEach { image ->
            val time = image.date?.toInstant()?.atOffset(ZoneOffset.UTC)?:OffsetDateTime.MIN
            if (time > lastModifiedTimestamp) {
                lastModifiedTimestamp = time
            }
        }
    }

    private fun sortImages() {
        val sort = determineSort()
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
                "mtime" -> images.sortBy { it.date }
            }
            if (sort.dir == SortDir.desc) {
                images.reverse()
            }
        }
    }

    private fun determineSort(): Sort {
        return sort?:Sort(by = "name", dir = SortDir.asc)
    }
}
