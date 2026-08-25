package de.visualdigits.photosite

import androidx.compose.runtime.Composable
import coil3.compose.setSingletonImageLoaderFactory
import de.visualdigits.common.domain.model.platform.PlatformType
import de.visualdigits.photosite.data.repository.ImageCache
import de.visualdigits.photosite.presentation.model.PhotositeViewModel
import de.visualdigits.photosite.presentation.page.MainPage
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App(
    platformType: PlatformType
) {
    val imageCache = koinInject<ImageCache>()
    val viewModel = koinViewModel<PhotositeViewModel>()

    setSingletonImageLoaderFactory { _ ->
        imageCache.getImageLoader()
    }

    MainPage(
        viewModel = viewModel,
        platformType = platformType
    )
}
