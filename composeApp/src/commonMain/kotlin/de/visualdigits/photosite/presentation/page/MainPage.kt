package de.visualdigits.photosite.presentation.page

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.visualdigits.common.domain.model.platform.PlatformType
import de.visualdigits.common.presentation.components.PlatformVerticalScrollbarBox
import de.visualdigits.common.presentation.components.button.IndicatorButton
import de.visualdigits.common.presentation.components.button.TabButtonRow
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.icon_hourglass_top_24px
import de.visualdigits.photosite.domain.model.photosite.ContentType
import de.visualdigits.photosite.domain.model.type.Language
import de.visualdigits.photosite.presentation.components.ErrorCard
import de.visualdigits.photosite.presentation.components.form.SettingsForm
import de.visualdigits.photosite.presentation.components.navigation.NavigationNode
import de.visualdigits.photosite.presentation.components.photo.PhotoCard
import de.visualdigits.photosite.presentation.components.photo.PhotoTab
import de.visualdigits.photosite.presentation.model.PhotositeAction
import de.visualdigits.photosite.presentation.model.PhotositeViewModel
import de.visualdigits.photosite.presentation.model.settings.SK
import de.visualdigits.photosite.presentation.style.AppCompositionProvider
import de.visualdigits.photosite.presentation.style.BackgroundColor
import de.visualdigits.photosite.presentation.style.BlueDark
import de.visualdigits.photosite.presentation.style.BlueLight
import de.visualdigits.photosite.presentation.style.BlueLighter
import de.visualdigits.photosite.presentation.style.BlueMedium
import de.visualdigits.photosite.presentation.style.MyShapes
import de.visualdigits.photosite.presentation.style.TextColor
import de.visualdigits.photosite.presentation.style.gap
import de.visualdigits.photosite.presentation.style.theme
import de.visualdigits.photosite.presentation.style.typography
import org.jetbrains.compose.resources.painterResource

@Composable
fun MainPage(
    viewModel: PhotositeViewModel,
    platformType: PlatformType
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val editedSettings by viewModel.editedSettings.collectAsStateWithLifecycle()
    val onAction = viewModel::onAction

    val hostUrl = settings?.get<String>(SK.hostUrl)
    val language = settings?.get<Language>(SK.language)

    val items = remember(state) {
        linkedMapOf<String, @Composable (() -> Unit)>(
            "tab_photos" to {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .width(300.dp),
                    ) {
                        PlatformVerticalScrollbarBox(
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
                            listOf(Pair("pages", @Composable {
                                NavigationNode(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    state = state,
                                    language = language,
                                    page = state.rootPage,
                                    onAction = viewModel::onAction,
                                    colorRoot = BlueLighter,
                                    colorFolder = BlueDark,
                                    colorLeaf = BlueMedium,
                                    textColorRoot = BlueDark,
                                    textColorFolder = Color.White,
                                    textColorLeaf = Color.Black,
                                )
                            }))
                        }
                    }

                    if (state.page?.content?.contentType == ContentType.LightGallery) {
                        PhotoTab(
                            modifier = Modifier
                                .weight(1f),
                            page = state.page,
                            hostUrl = hostUrl,
                            onAction = viewModel::onAction,
                            platformType = platformType,
                            viewModel = viewModel
                        )
                    }
                }
            },
            "tab_settings" to {
                SettingsForm(
                    platformType = platformType,
                    settings = editedSettings,
                    onAction = viewModel::onAction
                )
            }
        )
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val screenWidth = maxWidth
        val sizeFactor = when {
            screenWidth < 500.dp -> 0.9f
            else -> 1.0f
        }

        MaterialTheme(
            colorScheme = theme(
                backgroundColor = BackgroundColor,
                textColor = TextColor,
                spotColor = BlueLight
            ),
            typography = typography(
                textColor = TextColor,
                sizeFactor = sizeFactor
            ),
            shapes = MyShapes
        ) {
            AppCompositionProvider {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .safeDrawingPadding()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(MaterialTheme.shapes.gap)
                    ) {
//                        Image(
//                            modifier = Modifier
//                                .width(400.dp),
//                            painter = painterResource(Res.drawable.flyeralarm),
//                            contentDescription = null
//                        )

                        if (state.uiMessage != null) {
                            ErrorCard(
                                errorMessage = state.uiMessage,
                                severity = state.uiMessageSeverity,
                                shapeContainer = MaterialTheme.shapes.small
                            )
                        }

                        if (state.imageFile != null) {
                            PhotoCard(
                                image = state.imageFile,
                                hostUrl = hostUrl,
                                onAction = viewModel::onAction
                            )
                        } else {
                            TabButtonRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .drawBehind {
                                        val strokeWidth = 2.dp.toPx()
                                        drawLine(
                                            color = Color(0xaa111111),
                                            start = Offset(0f, size.height - strokeWidth / 2),
                                            end = Offset(size.width, size.height - strokeWidth / 2),
                                            strokeWidth = strokeWidth
                                        )
                                    },
                                horizontalArrangement = Arrangement.spacedBy(0.dp),
                                selectedTab = { state.selectedTabIndex },
                                loadingContent = if (isLoading) {
                                    {
                                        Icon(
                                            modifier = Modifier
                                                .width(200.dp)
                                                .height(200.dp),
                                            painter = painterResource(Res.drawable.icon_hourglass_top_24px),
                                            contentDescription = null,
                                            tint = BlueLight,
                                        )
                                    }
                                } else null,
                                items = items
                            ) { index ->
                                IndicatorButton(
                                    text = state.tabLabels[index].second.asString(),
                                    buttonColor = Color.Gray,
                                    indicatorPosition = Alignment.BottomCenter,
                                    indicatorColor = BlueLight,
                                    shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp, bottomStart = 0.dp, bottomEnd = 0.dp),
                                    selected = state.selectedTabIndex == index,
                                ) {
                                    onAction(
                                        PhotositeAction.OnTabSelected(index)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
