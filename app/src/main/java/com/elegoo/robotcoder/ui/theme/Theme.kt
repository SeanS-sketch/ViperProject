package com.elegoo.robotcoder.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = SlateBlue,
    onPrimary = CardLight,
    secondary = SoftTeal,
    onSecondary = CardLight,
    background = SurfaceLight,
    onBackground = SlateBlueDark,
    surface = CardLight,
    onSurface = SlateBlueDark,
    surfaceVariant = SurfaceLight,
    onSurfaceVariant = OnSurfaceMutedLight,
    outline = OnSurfaceMutedLight,
)

private val DarkColorScheme = darkColorScheme(
    primary = SoftTealDark,
    onPrimary = SlateBlueDark,
    secondary = SoftTeal,
    onSecondary = CardLight,
    background = SurfaceDark,
    onBackground = CardLight,
    surface = CardDark,
    onSurface = CardLight,
    surfaceVariant = SlateBlueDark,
    onSurfaceVariant = OnSurfaceMutedDark,
    outline = OnSurfaceMutedDark,
)

/**
 * Root Material 3 theme for the app. The Settings screen can override the
 * system preference by passing [darkTheme] explicitly.
 */
@Composable
fun RoverCoderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content,
    )
}
