package de.visualdigits.photosite

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import de.visualdigits.common.domain.model.platform.PlatformType
import de.visualdigits.photosite.presentation.model.PhotositeViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import kotlin.getValue

class MainActivity : AppCompatActivity() {

    private val viewModel: PhotositeViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = window.decorView
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->

                    // for newer device starting with android 14/15
                    enableEdgeToEdge(
                        statusBarStyle = SystemBarStyle.auto(
                            lightScrim = Color.TRANSPARENT,
                            darkScrim = Color.TRANSPARENT,
                            detectDarkMode = { false }
                        )
                    )

                    // for older devices down to android 6
                    val olderWindow = this@MainActivity.window
                    WindowCompat.getInsetsController(olderWindow, view).apply {
                        isAppearanceLightStatusBars = true
                    }
                }
            }
        }

        setContent {
            App(PlatformType.android)
        }
    }
}
