package de.visualdigits.kotlin.photosite.persistence.service

import de.visualdigits.kotlin.photosite.persistence.model.Page
import de.visualdigits.kotlin.photosite.persistence.repository.PageRepository
import org.springframework.stereotype.Service

@Service
class PageService(
    override val repository: PageRepository
) : AbstractBaseService<Page, String>(repository) {

    fun findByName(name: String): Set<Page> {
        return repository.findByName(name)
    }

    fun findByPath(path: String): Page? {
        return findByPath(path)
    }

    override fun saveIfNotExists(entity: Page): Page {
        val savedEntity = super.saveIfNotExists(entity)
        entity.children.forEach {
            it.parent = savedEntity
            saveIfNotExists(it)
        }
        return savedEntity
    }

    override fun findByUniqueConstraint(entity: Page): Page? {
        return repository.findByPath(entity.path!!)
    }
}
