package de.visualdigits.photosite.domain.data.model.common

import de.visualdigits.photosite.domain.data.serializer.LanguageSerializer
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
