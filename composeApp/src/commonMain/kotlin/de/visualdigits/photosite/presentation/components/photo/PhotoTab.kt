package de.visualdigits.photosite.presentation.components.photo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.visualdigits.common.domain.model.platform.PlatformType
import de.visualdigits.common.presentation.components.PlatformVerticalScrollbarBox
import de.visualdigits.common.presentation.components.modifier.beveledBorder
import de.visualdigits.common.presentation.components.util.conditional
import de.visualdigits.photosite.domain.model.photosite.PageUi
import de.visualdigits.photosite.presentation.components.Image
import de.visualdigits.photosite.presentation.model.PhotositeAction
import de.visualdigits.photosite.presentation.model.PhotositeViewModel
import de.visualdigits.photosite.presentation.style.BlueLighter
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
                    val aspect = (image.width.toDouble() / image.height.toDouble()).toFloat()
                    Box(
                        modifier = Modifier
                            .width(200.dp)
                            .height(200.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .beveledBorder(
                                    width = 200.dp,
                                    height = 200.dp,
                                    borderSize = 1.dp,
                                    shape = MaterialTheme.shapes.small
                                )
                                .width(200.dp)
                                .height(200.dp)
                                .background(Color(0xFF444444))
                                .pointerHoverIcon(PointerIcon.Hand)
                                .hoverable(interactionSource)
                                .clickable {
                                    onAction(PhotositeAction.OnImageClicked(image))
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            val shape = MaterialTheme.shapes.extraSmall
                            Image(
                                modifier = Modifier
                                    .conditional(isLandscape) { width(180.dp) }
                                    .conditional(isLandscape) { height(120.dp) }
                                    .conditional(!isLandscape) { width(120.dp) }
                                    .conditional(!isLandscape) { height(180.dp) }
                                    .conditional(isLandscape) { beveledBorder(
                                        width = 180.dp,
                                        height = 120.dp,
                                        borderSize = 1.dp,
                                        shape = shape,
                                        inset = true
                                    ) }
                                    .conditional(!isLandscape) { beveledBorder(
                                        width = 120.dp,
                                        height = 180.dp,
                                        borderSize = 1.dp,
                                        shape = shape,
                                        inset = true
                                    ) },
                                url = "$hostUrl/${image.thumbnailPath}",
                                contentDescription = image.name,
                                contentScale = if (isLandscape) ContentScale.FillWidth else ContentScale.FillHeight,
                                maxImageSize = 1024
                            )
                        }

                        Column(
                            modifier = Modifier
                                .conditional(!isLandscape) { rotate(-90.0f) }
                                .width(200.dp)
                                .height(200.dp)
                                .padding(10.dp),
                        ) {
                            val style = MaterialTheme.typography.bodySmall.copy(fontSize = MaterialTheme.typography.bodySmall.fontSize * 0.75)
                            val imageName = image.name.substringBeforeLast('.').replace("_", " ")
                            val imageDate = image.lastModified.format("yyyy-MM-dd")
                            Text(
                                text = "$imageName [$imageDate]",
                                style = style,
                                color = BlueLighter
                            )
                            Spacer(Modifier.weight(1f))
                            val aperture = image.apertureValue ?: ""
                            val exposure = image.exposureTime ?: ""
                            val iso = image.isoEquivalent?.let { "[ISO $it${if (image.exposureBias != "0 EV") " ${image.exposureBias}" else ""}]" }?:""
                            val focalLength = image.focalLength?.let { "@ $it" }?:""
                            Text(
                                text = "$aperture $exposure $iso $focalLength",
                                style = style,
                                color = BlueLighter
                            )
                        }
                    }
                }
            }
        }))
    }
}
