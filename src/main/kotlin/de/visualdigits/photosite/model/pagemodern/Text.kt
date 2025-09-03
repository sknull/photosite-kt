package de.visualdigits.photosite.model.pagemodern


class Text(
    val lang: String? = null,
    var value: String? = null
) {
    init {
        value = value?.trim()?.replace("\\n\\n +".toRegex(), "\n\n")
    }
}
