package de.visualdigits.photosite.data.photosite.model.plugins

import kotlinx.serialization.Serializable

@Serializable
data class LightBoxDescriptor(
    val resizeDuration: Long = 0,
    val fadeDuration: Long = 0,
    val imageFadeDuration: Long = 0,
    val wrapAround: Boolean = false
)
