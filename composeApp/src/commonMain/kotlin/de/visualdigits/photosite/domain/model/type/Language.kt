package de.visualdigits.photosite.domain.model.type

import de.visualdigits.common.domain.model.configuration.keyfactory.KeyFactory
import de.visualdigits.common.domain.model.ui.StringResourceEnumerable
import de.visualdigits.common.domain.model.ui.UiText
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.flag_bd
import de.visualdigits.compose.resources.flag_de
import de.visualdigits.compose.resources.flag_us
import de.visualdigits.compose.resources.language_bn
import de.visualdigits.compose.resources.language_de
import de.visualdigits.compose.resources.language_en
import de.visualdigits.photosite.domain.serializer.LanguageDeserializer
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.DrawableResource

@Serializable(with = LanguageDeserializer::class)
enum class Language(
    override val uiText: UiText,
    override val drawableResourceId: DrawableResource?,
    val localeCode: String
) : StringResourceEnumerable<Language> {

    DE(UiText.StringResourceId(Res.string.language_de), Res.drawable.flag_de, "de"),
    EN(UiText.StringResourceId(Res.string.language_en), Res.drawable.flag_us, "en"),
    BN(UiText.StringResourceId(Res.string.language_bn), Res.drawable.flag_bd, "bn"),
    ;

    companion object : KeyFactory<Language> {

        override val options: List<Triple<Language, UiText?, DrawableResource?>> = entries.map { e -> Triple(e, e.uiText, e.drawableResourceId) }

        override fun fromString(value: String?): Language? {
            return entries.find { e -> e.localeCode == value?.lowercase() }
        }

        override fun fromValue(value: Any?): Language? {
            return when (value) {
                is String -> fromString(value)
                is Language -> value
                else -> null
            }
        }

        override fun stringValue(value: Any?): String? {
            return (value as? Language)?.localeCode?:value?.toString()
        }
    }
}
