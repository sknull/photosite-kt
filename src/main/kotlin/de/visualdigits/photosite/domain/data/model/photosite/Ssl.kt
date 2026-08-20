package de.visualdigits.photosite.domain.data.model.photosite

data class Ssl(
    val certbotUri: String? = null,
    val keyStore: String? = null,
    val keyStoreType: String? = null,
    val keyAlias: String? = null,
    val keyStorePassword: String? = null
)
