package de.visualdigits.photosite.presentation.model

import androidx.compose.runtime.Immutable
import de.visualdigits.common.domain.model.ui.KeyValue
import de.visualdigits.common.domain.model.ui.UiText
import de.visualdigits.photosite.domain.model.photosite.ImageFileUi
import de.visualdigits.photosite.domain.model.photosite.PageUi
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
    // Pages
    //
    @Immutable
    data class OnPageClicked(
        val path: String,
    ): PhotositeAction

    //
    // Images
    //
    @Immutable
    data class OnImageClicked(
        val imageFile: ImageFileUi,
    ): PhotositeAction

    @Immutable
    class OnReturnClicked: PhotositeAction

    //
    // Navigation
    //

    @Immutable
    data class OnPageCollapsibleStateChange(
        val page: PageUi,
        val isExpanded: Boolean
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
