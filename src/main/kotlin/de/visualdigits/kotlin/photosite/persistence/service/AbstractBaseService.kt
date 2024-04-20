package de.visualdigits.kotlin.photosite.persistence.service

import de.visualdigits.kotlin.photosite.persistence.model.AbstractJpaPersistable
import org.springframework.data.jpa.repository.JpaRepository

abstract class AbstractBaseService<T : AbstractJpaPersistable, ID>(
    open val repository: JpaRepository<T, ID>
) {

    open fun saveIfNotExists(entity: T): T {
        var existingEntity = findByUniqueConstraint(entity)
        if (existingEntity == null) {
            existingEntity = repository.save(entity)
        }
        return existingEntity!!
    }

    fun delete(entity: T) {
        repository.delete(entity)
    }

    open fun deleteAll() {
        repository.deleteAll()
    }

    abstract fun findByUniqueConstraint(entity: T): T?
}
