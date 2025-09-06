package de.visualdigits.photosite.model.page.content

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import de.visualdigits.photosite.model.common.Translation
import java.util.Locale

@JsonIgnoreProperties(
    "translationsMap"
)
class Caption(
    val name: String? = null,
    val alt: String? = null,
    val caption: String? = null,
    @JsonAlias("i18n", "translations") val translations: List<Translation> = listOf()
) {

    lateinit var translationsMap: Map<Locale, Translation>

    init {
        translationsMap = translations.associateBy { t -> t.lang!! }
    }
}

