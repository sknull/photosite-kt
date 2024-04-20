package de.visualdigits.kotlin.photosite.persistence.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.Column
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import java.time.LocalDateTime
import java.util.UUID

/**
 * Abstract base class for all entities.
 * Adds the standard columns id, created and updated to all entities
 * and implements standard equals and hashcode methods which obey
 * the rules for hibernate entity classes.
 */
@MappedSuperclass
//@SoftDelete(columnName = "active", strategy = SoftDeleteType.ACTIVE)
@JsonIgnoreProperties("id", "created", "updated")
abstract class AbstractJpaPersistable {
    @Id @GeneratedValue(strategy = GenerationType.UUID) var id: UUID? = null

    @Column var created: LocalDateTime = LocalDateTime.now()

    @Column var updated: LocalDateTime = LocalDateTime.now()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractJpaPersistable) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
