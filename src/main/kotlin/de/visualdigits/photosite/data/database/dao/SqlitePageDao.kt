package de.visualdigits.photosite.data.database.dao

import de.visualdigits.photosite.data.database.model.PageEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface SqlitePageDao : CrudRepository<PageEntity, UUID> {

    fun findByPath(path: String): PageEntity?

    @Query("SELECT p FROM PageEntity p")
    fun findAllWithCollections(): List<PageEntity>
}
