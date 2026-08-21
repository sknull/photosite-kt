package de.visualdigits.photosite.domain.repository

import de.visualdigits.common.domain.model.errorhandling.Result
import de.visualdigits.photosite.domain.model.errorhandling.DataError
import de.visualdigits.photosite.presentation.model.settings.SettingsForm
import kotlinx.io.Sink
import kotlinx.io.Source

interface SettingsRepository {

    suspend fun getSettings(): Result<SettingsForm?, DataError.Local>

    suspend fun setSettings(settings: SettingsForm): Result<Unit, DataError.Local>

    suspend fun importSettings(ins: Source): Result<SettingsForm, DataError.Local>

    suspend fun exportSettings(settings: SettingsForm, outs: Sink): Result<Unit, DataError.Local>
}
