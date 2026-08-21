package de.visualdigits.photosite.domain.model

import androidx.compose.ui.graphics.Color
import de.visualdigits.common.domain.model.ui.UiText

data class DataMatrix<T>(
    val headers: List<UiText> = listOf(),
    val data: List<Pair<T, Map<String, String?>>> = listOf(),
    val textColor: Map<String, (T, String?) -> Color> = mapOf()
) {
    val columns: Int
        get() = headers.size

    val rows: Int
        get() = data.size
}
