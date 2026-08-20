package de.visualdigits.photosite.data.photosite.model

import de.visualdigits.photosite.data.filesystem.model.TranslationDescriptor
import kotlinx.serialization.Serializable

@Serializable
data class NavigationEntryDescriptor(
    val rootFolder: String? = null,
    val numberOfEntries: Int = 0,
    val translations: List<TranslationDescriptor> = listOf()
)
