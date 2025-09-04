package de.visualdigits.photosite.model.page

import java.util.Locale


class Text(
    val lang: Locale? = null,
    var value: String? = null
) {
    init {
        value = value?.trim()?.replace("\\n\\n +".toRegex(), "\n\n")
    }
}
