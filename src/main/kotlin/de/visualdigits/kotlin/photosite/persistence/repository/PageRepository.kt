package de.visualdigits.kotlin.photosite.persistence.repository

import de.visualdigits.kotlin.photosite.persistence.model.Page
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PageRepository : JpaRepository<Page, String> {

    fun findByName(name: String): Set<Page>

    fun findByPath(path: String): Page?
}
