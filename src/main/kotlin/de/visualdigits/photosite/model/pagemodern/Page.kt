package de.visualdigits.photosite.model.pagemodern

import com.fasterxml.jackson.annotation.JsonAlias
import java.time.OffsetDateTime


class Page(
    val icon: Any? = null,
    @JsonAlias("tocname", "tocName") val tocName: String? = null,
    var content: Content = Content(),
    val i18n: List<Lang> = listOf()
) {
    var level: Int = 0
    var name: String = ""

    var parent: Page? = null
    var children: List<Page> = listOf()

    override fun toString(): String {
        return "${"  ".repeat(level)}$name\n${children.joinToString("") { it.toString() }}"
    }
}
