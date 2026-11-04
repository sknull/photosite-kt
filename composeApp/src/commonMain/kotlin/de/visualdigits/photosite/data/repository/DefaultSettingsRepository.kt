package de.visualdigits.photosite.data.repository

import co.touchlab.kermit.Logger
import de.visualdigits.common.domain.model.configuration.AbstractConfiguration.Companion.valueMap
import de.visualdigits.common.domain.model.errorhandling.Result
import de.visualdigits.photosite.PhotositeQueries
import de.visualdigits.photosite.data.database.toSettingsEntity
import de.visualdigits.photosite.data.database.toSettingsForm
import de.visualdigits.photosite.data.database.upsertSettings
import de.visualdigits.photosite.domain.model.errorhandling.DataError
import de.visualdigits.photosite.domain.model.type.Language
import de.visualdigits.photosite.domain.repository.SettingsRepository
import de.visualdigits.photosite.presentation.model.settings.SK
import de.visualdigits.photosite.presentation.model.settings.SettingsForm
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readString
import kotlinx.io.writeString
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonPrimitive

class DefaultSettingsRepository(
    private val dao: PhotositeQueries
): SettingsRepository {

    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun getSettings(): Result<SettingsForm?, DataError.Local> = withContext(dispatcher) {
        try {
            dao.getSettingsById(0)
                .executeAsOneOrNull()
                ?.let { settingsEntity ->
                    settingsEntity
                        .toSettingsForm()
                        .let { s ->
                            Result.Success(s)
                        }
                } ?: Result.Success(null)
        } catch (e: Exception) {
            Logger.e("Could not load settings", e)
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun setSettings(settings: SettingsForm): Result<Unit, DataError.Local> = withContext(dispatcher) {
        try {
            val settingsEntity = settings.toSettingsEntity()
            dao.upsertSettings(settingsEntity)
            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e("Could not set settings", e)
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun importSettings(source: Source): Result<SettingsForm, DataError.Local> = withContext(dispatcher) {
        try {
            val jsonMapper = Json {
                ignoreUnknownKeys = true
                explicitNulls = false
            }
            val json = source.use { ins ->
                ins.readString()
            }

            val newValues = valueMap(
                fieldDescriptors = SettingsForm.DESCRIPTORS,
                values = jsonMapper
                    .decodeFromString<Map<String, JsonElement>>(json)
                    .mapNotNull { (key, value) ->
                        val sk = SK.fromString(key)
                        if (sk != null) {
                            Pair(sk, value.jsonPrimitive.content)
                        } else {
                            null
                        }
                    }
                    .toMap()
            )
            val settings = SettingsForm().initialize(SettingsForm.DESCRIPTORS, newValues)
            setSettings(settings)
            Result.Success(settings)
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN, e)
        }
    }

    override suspend fun exportSettings(settings: SettingsForm, sink: Sink): Result<Unit, DataError.Local> = withContext(dispatcher) {
        try {
            val jsonMapper = Json {
                prettyPrint = true
            }
            val value = settings.toSettingsRepositoryEntity()
            val json = jsonMapper.encodeToString(value)
            sink.use { writer ->
                writer.writeString(json)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN, e)
        }
    }
}

private fun SettingsForm.toSettingsRepositoryEntity(): SettingsRepositoryEntity {
    val settingsEntity = SettingsRepositoryEntity(
        id = 0,
        language = get<Language>(SK.language)?.name ?: "EN"
    )
    return settingsEntity
}

@Serializable
private data class SettingsRepositoryEntity(
    val id: Long,
    val language: String
)
