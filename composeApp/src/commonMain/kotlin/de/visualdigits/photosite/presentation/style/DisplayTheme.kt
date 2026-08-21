package de.visualdigits.photosite.presentation.style

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color


fun theme(
    backgroundColor: Color,
    textColor: Color,
    spotColor: Color
): ColorScheme = lightColorScheme(
    secondary = Color(0xFFE1E1E1), // switchbox unchecked track
    onSecondary = Color(0xFF9A9A9A), // switchbox unchecked thumb and border

    background = backgroundColor,
    onBackground = textColor,

    surface = Color.Transparent, // buttons
    onSurface = spotColor, // spot color

    surfaceContainer = Color.Transparent,
    surfaceContainerLowest = Color(0xFF797979),

    errorContainer = Color(0xffff002a), // delete dialogs
    onErrorContainer = Color(0xFFFFFFFF), // delete dialogs

    outline = textColor, // focused border

    primaryFixed = Color(0xAA000000) // terminal
)
