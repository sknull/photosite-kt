package de.visualdigits.kotlin.photosite.persistence.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.Transient
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(columnNames = [
            "path"
        ])
    ],
)
class Page(
    @Column
    val name: String? = null,

    @Column
    var path: String? = name,

    @Column
    val icon: String? = null,

    @ManyToOne
    @JoinColumn(name = "parent_page_id")
    var parent: Page? = null,

    @Transient
    var children: MutableSet<Page> = mutableSetOf()
): AbstractJpaPersistable() {

    fun determinePath() {
        path = rootLine().map { it.name }.joinToString("/")
    }

    fun rootLine(rootLine: MutableList<Page> = mutableListOf()): List<Page> {
        rootLine.add(0, this)
        parent?.rootLine(rootLine)
        return rootLine
    }

    fun withParent(parent: Page): Page {
        parent.children.add(this)
        this.parent = parent
        determinePath()
        return this
    }

    fun withChild(child: Page): Page {
        child.parent = this
        child.determinePath()
        children.add(child)
        return this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as Page

        return path == other.path
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (path?.hashCode() ?: 0)
        return result
    }
}
