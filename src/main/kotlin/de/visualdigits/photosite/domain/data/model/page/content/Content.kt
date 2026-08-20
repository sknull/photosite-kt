package de.visualdigits.photosite.domain.data.model.page.content

import de.visualdigits.photosite.domain.data.model.common.KmpOffsetDateTime
import java.io.File
import java.util.function.Consumer


data class Content(
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
    var htmlContent: String? = null,

    var images: MutableList<ImageFile> = mutableListOf()
) {
    var lastModified: KmpOffsetDateTime = KmpOffsetDateTime.MIN
    var captionsMap: Map<String, Caption> = mapOf()

    init {
        captions = captions.filter { c -> c.name?.isNotBlank() == true }
        captionsMap = captions.associate { c -> Pair(c.name!!, c) }
        keywords = keywords.firstOrNull()?.split(",")?.map { e -> e.trim() } ?: listOf()
    }

    fun calculateLastModified() {
        lastModified = images
            .maxOfOrNull { i -> i.lastModified }
            ?: KmpOffsetDateTime.MIN
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
