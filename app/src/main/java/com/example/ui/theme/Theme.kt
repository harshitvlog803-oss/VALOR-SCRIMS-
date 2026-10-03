package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ClashXColorScheme = darkColorScheme(
    primary = NeonFireOrange,
    onPrimary = TextPrimary,
    primaryContainer = NeonFireOrange.copy(alpha = 0.2f),
    onPrimaryContainer = NeonFireOrangeLight,
    secondary = NeonGold,
    onSecondary = GamingDarkBackground,
    secondaryContainer = NeonGold.copy(alpha = 0.2f),
    onSecondaryContainer = NeonGold,
    tertiary = ElectricCyan,
    onTertiary = GamingDarkBackground,
    background = GamingDarkBackground,
    onBackground = TextPrimary,
    surface = GamingSurface,
    onSurface = TextPrimary,
    surfaceVariant = GamingSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = BorderDark,
    error = DangerRed,
    onError = TextPrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = ClashXColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = GamingDarkBackground.toArgb()
                window.navigationBarColor = GamingDarkBackground.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
