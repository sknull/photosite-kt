package de.visualdigits.photosite.domain.model.photosite

import de.visualdigits.common.domain.model.common.KmpOffsetDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class PageUi(
    val icon: String? = null,
    val tocName: String? = null,
    val content: ContentUi? = null,
    val translations: List<TranslationUi> = listOf(),
    val level: Int = 0,
    val path: String = "/",
    val ariaName: String = "",
    val children: List<PageUi> = listOf(),
    val lastModified: KmpOffsetDateTime? = null
) {

    @Transient var parent: PageUi? = null

    fun collectPages(pageMap: MutableMap<String, PageUi> = mutableMapOf()): Map<String, PageUi> {
        pageMap[path] = this
        children.forEach { child ->
            child.parent = this
            child.collectPages(pageMap)
        }

        return pageMap
    }
}
