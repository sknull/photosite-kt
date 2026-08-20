package de.visualdigits.photosite.data.photosite.model

import kotlinx.serialization.Serializable

@Serializable
data class SslDescriptor(
    val certbotUri: String? = null,
    val keyStore: String? = null,
    val keyStoreType: String? = null,
    val keyAlias: String? = null,
    val keyStorePassword: String? = null
)
