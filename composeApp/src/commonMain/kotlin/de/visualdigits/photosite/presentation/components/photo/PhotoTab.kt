package de.visualdigits.photosite.presentation.components.photo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import de.visualdigits.common.domain.model.platform.PlatformType
import de.visualdigits.common.presentation.components.PlatformVerticalScrollbarBox
import de.visualdigits.photosite.domain.model.photosite.PageUi
import de.visualdigits.photosite.presentation.components.Image
import de.visualdigits.photosite.presentation.model.PhotositeAction
import de.visualdigits.photosite.presentation.model.PhotositeViewModel
import de.visualdigits.photosite.presentation.style.gap

@Composable
fun PhotoTab(
    modifier: Modifier = Modifier,
    platformType: PlatformType,
    viewModel: PhotositeViewModel,
    page: PageUi?,
    hostUrl: String?,
    onAction: (PhotositeAction) -> Unit
) {
    PlatformVerticalScrollbarBox(
        modifier = modifier
            .fillMaxSize(),
        platformType = platformType,
        backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
        scrollbarModifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .width(10.dp)
            .background(MaterialTheme.colorScheme.background)
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)),
        scrollbarId = "pages",
        scrollPosition = viewModel.scrollPosition,
        onCommonAction = viewModel::onCommonAction,
        verticalArrangementGap = 0.dp
    ) {
        listOf(Pair("photo_grid", @Composable {
            FlowRow(
                modifier = modifier
                    .fillMaxSize()
                    .padding(MaterialTheme.shapes.gap),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
            ) {
                page?.content?.images?.forEach { image ->
                    val interactionSource = remember { MutableInteractionSource() }
                    val isLandscape = image.width > image.height
                    println(image)
                    Box(
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .width(200.dp)
                            .height(200.dp)
                            .background(Color(0xFF444444))
                            .border(1.dp, Color(0xFFAAAAAA), MaterialTheme.shapes.small)
                            .pointerHoverIcon(PointerIcon.Hand)
                            .hoverable(interactionSource)
                            .clickable {
                                onAction(PhotositeAction.OnImageClicked(image))
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            modifier = Modifier
                                .fillMaxSize(),
                            url = "$hostUrl/${image.thumbnailPath}",
                            contentDescription = image.name,
                            contentScale = if (isLandscape) ContentScale.FillWidth else ContentScale.FillHeight,
                            maxImageSize = 1024
                        )
                    }
                }
            }
        }))
    }
}
