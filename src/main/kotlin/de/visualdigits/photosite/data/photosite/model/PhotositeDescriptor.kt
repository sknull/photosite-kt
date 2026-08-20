package de.visualdigits.photosite.data.photosite.model

import de.visualdigits.photosite.data.photosite.model.plugins.PluginsDescriptor
import de.visualdigits.photosite.domain.data.model.common.Language
import kotlinx.serialization.Serializable

@Serializable
data class PhotositeDescriptor(
    val serverPort: Int? = null,
    val internalPort: Int? = null,
    val internalEndpoints: List<String> = listOf(),
    val ssl: SslDescriptor? = null,
    val theme: String = "default",
    val siteTitle: String? = null,
    val siteSubTitle: String? = null,
    val protocol: String? = null,
    val domain: String? = null,
    val languages: List<Language> = listOf(),
    val languageDefault: Language = Language("de"),
    val naviMain: NavigationEntryDescriptor? = null,
    val naviSub: List<NavigationEntryDescriptor> = listOf(),
    val naviStatic: NavigationEntryDescriptor? = null,
    val plugins: PluginsDescriptor? = null
)
