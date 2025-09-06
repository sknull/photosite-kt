package de.visualdigits.photosite.model.page.content

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.io.File
import java.time.OffsetDateTime
import java.util.function.Consumer


@JsonIgnoreProperties(
    "descriptorFile",
    "directory",
    "files",
    "images",
    "lastModified",
    "captionsMap"
)
class Content(
    var contentType: ContentType? = null,
    val mode: String? = null,
    val speed: Int? = null,
    val pause: Int? = null,
    val download: Boolean? = null,
    val sort: Sort? = null,
    val teaser: Teaser? = null,
    var captions: List<Caption> = listOf(),
    var keywords: List<String> = listOf(),
    val paragraphs: List<Paragraph> = listOf(),
    var mdContent: String? = null,
    var htmlContent: String? = null
) {
    var descriptorFile: File? = null
    var directory: File? = null
    var files: Array<File> = arrayOf()

    var images: MutableList<ImageFile> = mutableListOf()

    var lastModified: OffsetDateTime = OffsetDateTime.MIN

    var captionsMap: Map<String, Caption>

    init {
        captions = captions.filter { c -> c.name?.isNotBlank() == true }
        captionsMap = captions.associate { c -> Pair(c.name!!, c) }
        keywords = keywords.firstOrNull()?.split(",")?.map { e -> e.trim() } ?: listOf()
    }

    fun loadContent() {
        val mdFile = File(directory, "page.md")
        if (mdFile.exists()) {
            contentType = ContentType.Markdown
            mdContent = mdFile.readText()
        }

        val htmlFile = File(directory, "page.html")
        if (htmlFile.exists()) {
            contentType = ContentType.Html
            htmlContent = htmlFile.readText()
        }
    }

    fun loadImages() {
        images = files
            .filter { f -> f.isFile && f.extension == "jpg" }
            .map { f -> ImageFile(f) }
            .toMutableList()
        lastModified = images
            .maxOfOrNull { i -> i.lastModified }
            ?: OffsetDateTime.MIN
    }

    fun sortImages() {
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
                "name" ->
                    images.sortBy { it.name }
                "mtime" ->
                    images.sortBy { it.lastModified }
            }
            if (sort.dir == SortDir.desc) {
                images.reverse()
            }
        }
    }
}
