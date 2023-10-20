package de.visualdigits.kotlin.photosite.model.page

import org.springframework.stereotype.Component
import java.io.File

@Component
class PageFactory {

    private val pageCache = mutableMapOf<File, Page>()

    fun load(pageDirectory: File?, descriptorFile: File): Page? {
        var page = pageCache[pageDirectory]
        if (page == null) {
            page = Page.load(descriptorFile)
            pageDirectory
                ?.let { file ->
                    page?.let { p ->pageCache[file] = p }
                }
        }
        return page
    }

    fun clearPageCache() {
        pageCache.clear()
    }
}
