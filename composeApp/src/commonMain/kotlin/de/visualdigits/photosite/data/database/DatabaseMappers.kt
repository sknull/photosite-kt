package de.visualdigits.photosite.data.database

import de.visualdigits.common.domain.model.configuration.AbstractConfiguration.Companion.valueMap
import de.visualdigits.photosite.SettingsEntity
import de.visualdigits.photosite.domain.model.type.Language
import de.visualdigits.photosite.presentation.model.settings.SK
import de.visualdigits.photosite.presentation.model.settings.SettingsForm

fun SettingsEntity.toSettingsForm(): SettingsForm {
    val newValues = valueMap(
        fieldDescriptors = SettingsForm.DESCRIPTORS,
        values = mapOf(
            SK.language to Language.fromValue(language),
            SK.hostUrl to hostUrl,
        )
    )
    return SettingsForm().initialize(SettingsForm.DESCRIPTORS, newValues)
}

fun SettingsForm.toSettingsEntity(): SettingsEntity {
    val settingsEntity = SettingsEntity(
        id = 0,
        language = get<Language>(SK.language)?.localeCode ?: "en",
        hostUrl = get<String>(SK.hostUrl),
    )
    return settingsEntity
}
