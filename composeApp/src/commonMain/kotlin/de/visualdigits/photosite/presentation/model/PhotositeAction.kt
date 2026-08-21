package de.visualdigits.photosite.presentation.model

import androidx.compose.runtime.Immutable
import de.visualdigits.common.domain.model.ui.KeyValue
import de.visualdigits.common.domain.model.ui.UiText
import kotlinx.io.Sink
import kotlinx.io.Source

sealed interface PhotositeAction {

    //
    // Tabs
    //
    @Immutable
    data class OnTabSelected(
        val index: Int
    ): PhotositeAction

    @Immutable
    data class OnInitializeTabs(
        val tabLabels: List<Pair<String, UiText>>
    ): PhotositeAction

    //
    // Settings
    //
    @Immutable
    data class OnSettingsValueChanged(
        val keyValue: KeyValue,
    ): PhotositeAction

    @Immutable
    class OnEditSettingsCancelClick : PhotositeAction

    @Immutable
    class OnSaveSettingsClick : PhotositeAction

    @Immutable
    data class OnSettingsImport(
        val fileName: String,
        val source: Source
    ): PhotositeAction

    @Immutable
    data class OnSettingsExport(
        val fileName: String,
        val sink: Sink
    ): PhotositeAction
}
