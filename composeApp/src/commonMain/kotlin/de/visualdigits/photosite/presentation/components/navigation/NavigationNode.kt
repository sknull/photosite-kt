package de.visualdigits.photosite.presentation.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.visualdigits.common.presentation.components.button.IndicatorButton
import de.visualdigits.common.presentation.components.container.VerticalCollapsibleBox
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.icon_arrow_drop_down_24px
import de.visualdigits.compose.resources.icon_arrow_right_24px
import de.visualdigits.photosite.domain.model.photosite.PageUi
import de.visualdigits.photosite.domain.model.type.Language
import de.visualdigits.photosite.presentation.model.PhotositeAction
import de.visualdigits.photosite.presentation.model.PhotositeState
import de.visualdigits.photosite.presentation.style.gap
import org.jetbrains.compose.resources.painterResource

/**
 * Renders the collapsible container for a given  newsfeed group.
 */
@Composable
fun NavigationNode(
    modifier: Modifier = Modifier,
    state: PhotositeState,
    language: Language?,
    page: PageUi?,
    level: Int = 0,
    colorRoot: Color,
    colorFolder: Color,
    colorLeaf: Color,
    textColorFolder: Color,
    textColorRoot: Color,
    textColorLeaf: Color,
    onAction: (PhotositeAction) -> Unit
) {
    if (page == null) return

    val collapsibleState = state.collapsibleState["group_${page.path.replace("/", "_")}"]
    val isRoot = page.parent == null
    val isLeaf = page.children.isEmpty()
    val label = page.translationMap[language]?.title
        ?: page.translationMap[language]?.name
        ?: page.path.substringAfterLast("/")
    val color = if (isRoot) colorRoot else if (isLeaf) colorLeaf else colorFolder
    val textColor = if (isRoot) textColorRoot else if (isLeaf) textColorLeaf else textColorFolder
    VerticalCollapsibleBox(
        modifier = modifier
            .fillMaxWidth(), // indent
        modifierHeader = Modifier
            .background(color)
            .padding(start = if (!isRoot) 20.dp else 0.dp),
        isTitleHoverable = true,
        titleHoverColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        titleContent = {
            IndicatorButton(
                isHoverable = false,
                width = 180.dp - MaterialTheme.shapes.gap * 2,
                height = 30.dp,
                indicatorPosition = Alignment.CenterStart,
                indicatorColor = MaterialTheme.colorScheme.onSurface,
                text = label,
                textStyle = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Start,
                textColor = textColor,
                buttonColor = Color.Transparent,
                shape = RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp),
                selected = state.page?.path == page.path
            )
        },
        focusedBorderColor = Color.Transparent,
        unfocusedBorderColor = Color.Transparent,
        backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
        shape = MaterialTheme.shapes.small,
        space = 1.dp,
        onStateChange = { state ->
            onAction(PhotositeAction.OnPageCollapsibleStateChange(page, state))
        },
        isExpanded = collapsibleState == true,
        iconArrowRight = if (page.children.isNotEmpty()) painterResource(Res.drawable.icon_arrow_right_24px) else null,
        iconArrowDown = if (page.children.isNotEmpty()) painterResource(Res.drawable.icon_arrow_drop_down_24px) else null,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            page.children
                .groupBy { it.children.isNotEmpty() }
                .toList()
                .sortedByDescending { it.first }
                .map { it.second }
                .forEach { pages ->
                    pages.forEach { page ->
                        NavigationNode(
                            modifier = modifier,
                            state = state,
                            language = language,
                            page = page,
                            level = level + 1,
                            onAction = onAction,
                            colorRoot = colorRoot,
                            colorFolder = colorFolder,
                            colorLeaf = colorLeaf,
                            textColorRoot = textColorRoot,
                            textColorFolder = textColorFolder,
                            textColorLeaf = textColorLeaf
                        )
                    }
                }
        }
    }
}
