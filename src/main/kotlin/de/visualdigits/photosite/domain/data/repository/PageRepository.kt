package de.visualdigits.photosite.domain.data.repository

import de.visualdigits.photosite.domain.data.model.page.Page
import org.springframework.stereotype.Service
import java.util.UUID

@Service
interface PageRepository {

    fun getPages(): List<Page>

    fun getPagesEager(): List<Page>

    fun getPage(id: UUID): Page?

    fun addPageTree(page: Page)

    fun updatePage(page: Page)

    fun deletePage(id: UUID)
}
