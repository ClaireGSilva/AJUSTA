package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val DarkColorScheme = darkColorScheme(
    primary = AtelierWarmWhite,
    onPrimary = AtelierBlack,
    primaryContainer = Color(0xFF2C2A26),
    onPrimaryContainer = AtelierWarmWhite,
    secondary = AtelierBrassLight,
    onSecondary = AtelierBlack,
    secondaryContainer = Color(0xFF383226),
    onSecondaryContainer = AtelierBrassLight,
    tertiary = AtelierForest,
    background = Color(0xFF141312),
    onBackground = Color(0xFFF7F5F0),
    surface = Color(0xFF1E1D19),
    onSurface = Color(0xFFF7F5F0),
    surfaceVariant = Color(0xFF282622),
    onSurfaceVariant = Color(0xFFC0BCB3),
    outline = Color(0xFF403C35),
    outlineVariant = Color(0xFF2C2A26)
)

val LightColorScheme = lightColorScheme(
    primary = AtelierBlack,
    onPrimary = AtelierWarmWhite,
    primaryContainer = AtelierLinen,
    onPrimaryContainer = AtelierBlack,
    secondary = AtelierBrass,
    onSecondary = AtelierWarmWhite,
    secondaryContainer = AtelierBrassContainer,
    onSecondaryContainer = AtelierBrass,
    tertiary = AtelierForest,
    background = AtelierWarmWhite,
    onBackground = AtelierBlack,
    surface = Color(0xFFFFFFFF),
    onSurface = AtelierBlack,
    surfaceVariant = AtelierLinen,
    onSurfaceVariant = AtelierWarmGray,
    outline = AtelierLightBorder,
    outlineVariant = AtelierSand
)

data class AtelierThemeColors(
    val cardBackground: Color,
    val cardSubtleBackground: Color,
    val cardBorder: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val isDark: Boolean
)

val LocalAtelierThemeColors = staticCompositionLocalOf {
    AtelierThemeColors(
        cardBackground = Color.White,
        cardSubtleBackground = AtelierLinen,
        cardBorder = AtelierLightBorder,
        textPrimary = AtelierBlack,
        textSecondary = AtelierOffBlack,
        textMuted = AtelierWarmGray,
        isDark = false
    )
}

@Composable
fun AjustaTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val atelierThemeColors = if (darkTheme) {
        AtelierThemeColors(
            cardBackground = Color(0xFF1E1D19),
            cardSubtleBackground = Color(0xFF282622),
            cardBorder = Color(0xFF38352F),
            textPrimary = Color(0xFFF7F5F0),
            textSecondary = Color(0xFFE2DDD5),
            textMuted = Color(0xFF9E9A91),
            isDark = true
        )
    } else {
        AtelierThemeColors(
            cardBackground = Color.White,
            cardSubtleBackground = AtelierLinen,
            cardBorder = AtelierLightBorder,
            textPrimary = AtelierBlack,
            textSecondary = AtelierOffBlack,
            textMuted = AtelierWarmGray,
            isDark = false
        )
    }

    CompositionLocalProvider(LocalAtelierThemeColors provides atelierThemeColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

// Backward compatibility alias
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    AjustaTheme(
        themeMode = if (darkTheme) AppThemeMode.DARK else AppThemeMode.LIGHT,
        content = content
    )
}
