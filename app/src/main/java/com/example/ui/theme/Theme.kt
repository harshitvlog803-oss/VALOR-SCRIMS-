package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val LocalIsWhiteTheme = compositionLocalOf { false }

fun appBackground(isWhite: Boolean): Color = if (isWhite) GamingWhiteBackground else GamingDarkBackground
fun appSurface(isWhite: Boolean): Color = if (isWhite) GamingWhiteSurface else GamingSurface
fun appCard(isWhite: Boolean): Color = if (isWhite) GamingWhiteCard else GamingCard
fun appCardElevated(isWhite: Boolean): Color = if (isWhite) GamingWhiteCardElevated else GamingCardElevated
fun appBorder(isWhite: Boolean): Color = if (isWhite) GamingWhiteBorder else BorderDark
fun appTextPrimary(isWhite: Boolean): Color = if (isWhite) TextPrimaryDark else TextPrimary
fun appTextSecondary(isWhite: Boolean): Color = if (isWhite) TextSecondaryDark else TextSecondary
fun appTextTertiary(isWhite: Boolean): Color = if (isWhite) TextTertiaryDark else TextTertiary

private val ClashXDarkColorScheme = darkColorScheme(
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

private val ClashXWhiteColorScheme = lightColorScheme(
    primary = NeonFireOrange,
    onPrimary = Color.White,
    primaryContainer = NeonFireOrange.copy(alpha = 0.15f),
    onPrimaryContainer = NeonFireOrange,
    secondary = NeonGold,
    onSecondary = Color.Black,
    secondaryContainer = NeonGold.copy(alpha = 0.15f),
    onSecondaryContainer = Color(0xFFB45309),
    tertiary = ElectricCyan,
    onTertiary = Color.Black,
    background = GamingWhiteBackground,
    onBackground = TextPrimaryDark,
    surface = GamingWhiteSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = GamingWhiteCardElevated,
    onSurfaceVariant = TextSecondaryDark,
    outline = GamingWhiteBorder,
    error = DangerRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    isWhiteTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (isWhiteTheme) ClashXWhiteColorScheme else ClashXDarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val bg = if (isWhiteTheme) GamingWhiteBackground else GamingDarkBackground
                window.statusBarColor = bg.toArgb()
                window.navigationBarColor = bg.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = isWhiteTheme
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = isWhiteTheme
            }
        }
    }

    CompositionLocalProvider(LocalIsWhiteTheme provides isWhiteTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
