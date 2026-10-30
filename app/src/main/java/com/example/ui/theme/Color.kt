package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Light Glass Theme Colors
val LightBackgroundStart = Color(0xFFF1F5F9)
val LightBackgroundEnd = Color(0xFFE2E8F0)
val LightGlassSurface = Color(0xE6FFFFFF)
val LightGlassSurfaceVariant = Color(0xB8FFFFFF)
val LightGlassBorder = Color(0x66FFFFFF)
val LightPrimary = Color(0xFF2563EB)
val LightSecondary = Color(0xFF0D9488)
val LightTertiary = Color(0xFF7C3AED)
val LightOnBackground = Color(0xFF0F172A)
val LightOnSurface = Color(0xFF1E293B)
val LightTextMuted = Color(0xFF64748B)

// Dark Glass Theme Colors
val DarkBackgroundStart = Color(0xFF0B132B)
val DarkBackgroundMid = Color(0xFF1C2541)
val DarkBackgroundEnd = Color(0xFF111D4A)
val DarkGlassSurface = Color(0x331C2541)
val DarkGlassSurfaceVariant = Color(0x4D3A506B)
val DarkGlassBorder = Color(0x336FFFE9)
val DarkPrimary = Color(0xFF48CAE4)
val DarkSecondary = Color(0xFF00B4D8)
val DarkTertiary = Color(0xFF90E0EF)
val DarkOnBackground = Color(0xFFF8FAFC)
val DarkOnSurface = Color(0xFFE2E8F0)
val DarkTextMuted = Color(0xFF94A3B8)

// Status & Semantic Colors
val SuccessGreen = Color(0xFF10B981)
val WarningAmber = Color(0xFFF59E0B)
val ErrorRed = Color(0xFFEF4444)
val InfoCyan = Color(0xFF06B6D4)

// Card Gradients
val GlassGradientLight = Brush.verticalGradient(
    colors = listOf(
        Color(0xDDFFFFFF),
        Color(0x99F8FAFC)
    )
)

val GlassGradientDark = Brush.verticalGradient(
    colors = listOf(
        Color(0x553A506B),
        Color(0x221C2541)
    )
)

val PrimaryGradientLight = Brush.horizontalGradient(
    colors = listOf(Color(0xFF2563EB), Color(0xFF3B82F6))
)

val PrimaryGradientDark = Brush.horizontalGradient(
    colors = listOf(Color(0xFF0077B6), Color(0xFF0096C7))
)

val AccentGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF059669), Color(0xFF10B981))
)
