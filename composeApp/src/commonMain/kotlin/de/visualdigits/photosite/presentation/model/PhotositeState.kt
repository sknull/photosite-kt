package de.visualdigits.photosite.presentation.model

import androidx.compose.runtime.Stable
import co.touchlab.kermit.Severity
import de.visualdigits.common.domain.model.ui.UiText
import de.visualdigits.photosite.domain.model.photosite.PageUi

@Stable
data class PhotositeState(

    val previousSelectedTabIndexes: List<Int> = listOf(),
    val selectedTabIndex: Int = 0,
    val tabLabels: List<Pair<String, UiText>> = listOf(),
    val tabLabelKeys: List<String> = listOf(),

    val pageTree: PageUi? = null,

    val isEditingSettings: Boolean = false,

    val uiMessage: UiText? = null,
    val uiMessageSeverity: Severity? = null,
) {
    fun tabIndex(key: String): Int {
        return tabLabels
            .find { tl -> tl.first == key }
            ?.let { pair -> tabLabels.indexOf(pair) }
            ?: 0
    }
}
