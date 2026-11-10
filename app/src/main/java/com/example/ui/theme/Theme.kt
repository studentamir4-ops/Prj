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
    val isDark: Boolean,
    val activePalette: ThemePalette
)

val LocalGlassTokens = staticCompositionLocalOf {
    GlassThemeTokens(
        glassSurface = LightGlassSurface,
        glassSurfaceVariant = LightGlassSurfaceVariant,
        glassBorder = LightGlassBorder,
        backgroundGradient = Brush.verticalGradient(listOf(LightBackgroundStart, LightBackgroundEnd)),
        cardGradient = GlassGradientLight,
        textMuted = LightTextMuted,
        isDark = false,
        activePalette = ThemePalette.OCEAN_BLUE
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    palette: ThemePalette = ThemePalette.OCEAN_BLUE,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = palette.darkPrimary,
            onPrimary = Color.Black,
            primaryContainer = palette.darkContainer,
            onPrimaryContainer = Color.White,
            secondary = palette.darkSecondary,
            onSecondary = Color.Black,
            secondaryContainer = palette.darkContainer,
            onSecondaryContainer = Color.White,
            tertiary = palette.darkSecondary,
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
    } else {
        lightColorScheme(
            primary = palette.lightPrimary,
            onPrimary = Color.White,
            primaryContainer = palette.lightContainer,
            onPrimaryContainer = palette.lightPrimary,
            secondary = palette.lightSecondary,
            onSecondary = Color.White,
            secondaryContainer = palette.lightContainer,
            onSecondaryContainer = palette.lightPrimary,
            tertiary = palette.lightSecondary,
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
    }

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
            isDark = true,
            activePalette = palette
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
            isDark = false,
            activePalette = palette
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
