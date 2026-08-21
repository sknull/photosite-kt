package de.visualdigits.photosite

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import co.touchlab.kermit.Logger
import com.formdev.flatlaf.FlatDarculaLaf
import de.visualdigits.photosite.di.platformModule
import de.visualdigits.photosite.di.sharedModule
import de.visualdigits.common.domain.model.platform.PlatformType
import de.visualdigits.common.domain.service.getPlatformLogWriters
import kotlinx.coroutines.cancel
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import javax.swing.UIManager

fun main() {
    val koinApp = startKoin {
        modules(sharedModule, platformModule)
    }
    val homeDirectoryPath = koinApp.koin.get<String>(named("homeDirectory"))
    val writers = getPlatformLogWriters(homeDirectoryPath, "FlyerAlarmChallenge.log")
    Logger.setLogWriters(writers)
    Logger.setTag("FAC")

    application {
        val ioScope = rememberCoroutineScope()
        val state = rememberWindowState(
            width = 1200.dp,
            height = 900.dp,
            position = WindowPosition(Alignment.Center)
        )

        UIManager.setLookAndFeel(FlatDarculaLaf())

        Window(
            onCloseRequest = {
                ioScope.cancel("Normal Exit")
                exitApplication()
            },
            title = "Flyeralarm Challenge",
            state = state
        ) {
            App(PlatformType.jvm)
        }
    }
}
