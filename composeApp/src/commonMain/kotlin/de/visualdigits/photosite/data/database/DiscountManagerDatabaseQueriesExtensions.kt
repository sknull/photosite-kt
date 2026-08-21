package de.visualdigits.photosite.data.database

import de.visualdigits.photosite.PhotositeQueries
import de.visualdigits.photosite.SettingsEntity

fun PhotositeQueries.upsertSettings(settingsEntity: SettingsEntity) {
    val entity = getSettingsById(settingsEntity.id).executeAsOneOrNull()
    if (entity != null) {
        updateSettings(settingsEntity)
    } else {
        insertSettings(settingsEntity)
    }
}

fun PhotositeQueries.insertSettings(settingsEntity: SettingsEntity) {
    insertSettings(
        language = settingsEntity.language,
        hostUrl = settingsEntity.hostUrl
    )
}

fun PhotositeQueries.updateSettings(settingsEntity: SettingsEntity) {
    updateSettingsEntity(
        language = settingsEntity.language,
        hostUrl = settingsEntity.hostUrl,
        id = settingsEntity.id
    )
}
