package de.visualdigits.photosite

import androidx.compose.runtime.Composable
import de.visualdigits.common.domain.model.platform.PlatformType
import de.visualdigits.photosite.presentation.model.PhotositeViewModel
import de.visualdigits.photosite.presentation.page.MainPage
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App(
    platformType: PlatformType
) {

    val viewModel = koinViewModel<PhotositeViewModel>()

    MainPage(
        viewModel = viewModel,
        platformType = platformType
    )
}
