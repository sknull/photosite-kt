package de.visualdigits.photosite.data.database.repository

import de.visualdigits.photosite.data.database.dao.SqlitePageDao
import de.visualdigits.photosite.data.database.mapper.toPage
import de.visualdigits.photosite.data.database.mapper.toPageEntity
import de.visualdigits.photosite.data.database.model.PageEntity
import de.visualdigits.photosite.domain.data.model.page.Page
import de.visualdigits.photosite.domain.data.repository.PageRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class DefaultPageRepository(
    private val dao: SqlitePageDao
) : PageRepository {

    override fun getPages(): List<Page> {
        return dao.findAll().map { it.toPage() }
    }

    override fun getPagesEager(): List<Page> {
        return dao.findAllWithCollections().map { it.toPage() }
    }

    override fun getPage(id: UUID): Page? {
        return dao.findById(id).orElse(null)?.toPage()
    }

    @Transactional
    override fun addPageTree(page: Page) {
        page.collectEntities().forEach { pe ->
            val existingPage = dao.findByPath(pe.path)
            val pageToSave = existingPage?.let { ep -> pe.copy(id = ep.id) } ?: pe
            dao.save(pageToSave)
        }
    }

    private fun Page.collectEntities(allEntities: MutableList<PageEntity> = mutableListOf()): List<PageEntity> {
        allEntities.add(toPageEntity())
        children.forEach { child -> child.collectEntities(allEntities) }

        return allEntities
    }

    override fun updatePage(page: Page) {
        dao.save(page.toPageEntity())
    }

    override fun deletePage(id: UUID) {
        dao.deleteById(id)
    }
}
