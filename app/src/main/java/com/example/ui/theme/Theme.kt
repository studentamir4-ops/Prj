package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Immutable
data class GlassThemeTokens(
    val glassSurface: Color,
    val glassSurfaceVariant: Color,
    val glassBorder: Color,
    val backgroundGradient: Brush,
    val cardGradient: Brush,
    val textMuted: Color,
    val isDark: Boolean
)

val LocalGlassTokens = staticCompositionLocalOf {
    GlassThemeTokens(
        glassSurface = LightGlassSurface,
        glassSurfaceVariant = LightGlassSurfaceVariant,
        glassBorder = LightGlassBorder,
        backgroundGradient = Brush.verticalGradient(listOf(LightBackgroundStart, LightBackgroundEnd)),
        cardGradient = GlassGradientLight,
        textMuted = LightTextMuted,
        isDark = false
    )
}

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF1E3A5F),
    onPrimaryContainer = Color(0xFFD0E4FF),
    secondary = DarkSecondary,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF0F3B5F),
    onSecondaryContainer = Color(0xFFBCE9FF),
    tertiary = DarkTertiary,
    onTertiary = Color.Black,
    background = DarkBackgroundStart,
    onBackground = DarkOnBackground,
    surface = DarkGlassSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkGlassSurfaceVariant,
    onSurfaceVariant = DarkOnSurface,
    outline = DarkGlassBorder,
    error = ErrorRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF1E3A8A),
    secondary = LightSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCCFBF1),
    onSecondaryContainer = Color(0xFF115E59),
    tertiary = LightTertiary,
    onTertiary = Color.White,
    background = LightBackgroundStart,
    onBackground = LightOnBackground,
    surface = LightGlassSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightGlassSurfaceVariant,
    onSurfaceVariant = LightOnSurface,
    outline = LightGlassBorder,
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val glassTokens = if (darkTheme) {
        GlassThemeTokens(
            glassSurface = DarkGlassSurface,
            glassSurfaceVariant = DarkGlassSurfaceVariant,
            glassBorder = DarkGlassBorder,
            backgroundGradient = Brush.verticalGradient(
                listOf(DarkBackgroundStart, DarkBackgroundMid, DarkBackgroundEnd)
            ),
            cardGradient = GlassGradientDark,
            textMuted = DarkTextMuted,
            isDark = true
        )
    } else {
        GlassThemeTokens(
            glassSurface = LightGlassSurface,
            glassSurfaceVariant = LightGlassSurfaceVariant,
            glassBorder = LightGlassBorder,
            backgroundGradient = Brush.verticalGradient(
                listOf(LightBackgroundStart, LightBackgroundEnd)
            ),
            cardGradient = GlassGradientLight,
            textMuted = LightTextMuted,
            isDark = false
        )
    }

    CompositionLocalProvider(LocalGlassTokens provides glassTokens) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}
