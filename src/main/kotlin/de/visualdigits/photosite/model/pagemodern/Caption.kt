package de.visualdigits.photosite.model.pagemodern

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(
    "translationsMap"
)
class Caption(
    val name: String? = null,
    val alt: String? = null,
    val caption: String? = null,
    @JsonAlias("i18n", "translations") val translations: List<Translation> = listOf()
) {
    val translationsMap = translations.associateBy { t -> t.lang }
}

