package de.visualdigits.photosite.data.repository

import de.visualdigits.common.domain.model.configuration.AbstractConfiguration.Companion.valueMap
import de.visualdigits.common.domain.model.errorhandling.Result
import de.visualdigits.photosite.domain.model.errorhandling.DataError
import de.visualdigits.photosite.domain.model.type.Language
import de.visualdigits.photosite.domain.repository.SettingsRepository
import de.visualdigits.photosite.presentation.model.settings.SK
import de.visualdigits.photosite.presentation.model.settings.SettingsForm
import io.ktor.utils.io.readText
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.serialization.json.Json
import java.io.File

class TestSettingsRepository : SettingsRepository {

    companion object {

        private val jsonMapper = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            encodeDefaults = true
        }

        private val credentials = jsonMapper.decodeFromString<Map<String, String>>(File("E:\\restoreworkstation\\_Serial\\opensky_credentials.json").readText())

        private var settings = SettingsForm(
            valueMap(
                fieldDescriptors = SettingsForm.DESCRIPTORS,
                values = mapOf(
                    SK.language to Language.EN,
                    SK.hostUrl to "http://localhost:8080"
                )
            )
        )
    }

    override suspend fun getSettings(): Result<SettingsForm?, DataError.Local> {
        return Result.Success(settings)
    }

    override suspend fun setSettings(settings: SettingsForm): Result<Unit, DataError.Local> {
        TestSettingsRepository.settings =  settings
        return Result.Success(Unit)
    }

    override suspend fun importSettings(ins: Source): Result<SettingsForm, DataError.Local> {
        val settings = jsonMapper.decodeFromString<SettingsForm>(ins.readText())
        return Result.Success(settings)
    }

    override suspend fun exportSettings(
        settings: SettingsForm,
        outs: Sink
    ): Result<Unit, DataError.Local> {
        return Result.Success(Unit)
    }
}
