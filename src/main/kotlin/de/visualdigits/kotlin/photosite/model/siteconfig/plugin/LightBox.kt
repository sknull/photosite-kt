package de.visualdigits.kotlin.photosite.model.siteconfig.plugin

class LightBox(
    val resizeDuration: Long,
    val fadeDuration: Long,
    val imageFadeDuration: Long,
    val wrapAround: Boolean
) : Plugin(
    name = "LightBox",
    clazz = "de.visualdigits.kotlin.photosite.model.siteconfig.plugin.LightBox"
)
