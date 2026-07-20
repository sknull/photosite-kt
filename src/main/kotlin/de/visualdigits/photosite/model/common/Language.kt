package de.visualdigits.photosite.model.common

import de.visualdigits.photosite.serializer.LanguageSerializer
import kotlinx.serialization.Serializable
import java.util.Locale

@Serializable(with = LanguageSerializer::class)
data class Language(
    val language: String
) {

    val locale: Locale
        get() = Locale.forLanguageTag(language)

    override fun toString(): String {
        return language
    }
}
