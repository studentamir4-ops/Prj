package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Base background colors
val LightBackgroundStart = Color(0xFFF8FAFC)
val LightBackgroundEnd = Color(0xFFEDF2F7)
val LightGlassSurface = Color(0xF2FFFFFF)
val LightGlassSurfaceVariant = Color(0xD9FFFFFF)
val LightGlassBorder = Color(0x66FFFFFF)
val LightOnBackground = Color(0xFF0F172A)
val LightOnSurface = Color(0xFF1E293B)
val LightTextMuted = Color(0xFF64748B)

val DarkBackgroundStart = Color(0xFF0B132B)
val DarkBackgroundMid = Color(0xFF1C2541)
val DarkBackgroundEnd = Color(0xFF111D4A)
val DarkGlassSurface = Color(0x331C2541)
val DarkGlassSurfaceVariant = Color(0x4D3A506B)
val DarkGlassBorder = Color(0x336FFFE9)
val DarkOnBackground = Color(0xFFF8FAFC)
val DarkOnSurface = Color(0xFFE2E8F0)
val DarkTextMuted = Color(0xFF94A3B8)

// Semantic colors
val SuccessGreen = Color(0xFF10B981)
val WarningAmber = Color(0xFFF59E0B)
val ErrorRed = Color(0xFFEF4444)
val InfoCyan = Color(0xFF06B6D4)

// Card Glass Gradients
val GlassGradientLight = Brush.verticalGradient(
    colors = listOf(
        Color(0xF0FFFFFF),
        Color(0xC8F8FAFC)
    )
)

val GlassGradientDark = Brush.verticalGradient(
    colors = listOf(
        Color(0x553A506B),
        Color(0x221C2541)
    )
)

// Supported Theme Palettes
enum class ThemePalette(
    val title: String,
    val description: String,
    val previewColor: Color,
    val lightPrimary: Color,
    val lightSecondary: Color,
    val darkPrimary: Color,
    val darkSecondary: Color,
    val lightContainer: Color,
    val darkContainer: Color
) {
    OCEAN_BLUE(
        title = "آبی اقیانوسی",
        description = "رنگ پیش‌فرض و مدرن",
        previewColor = Color(0xFF2563EB),
        lightPrimary = Color(0xFF2563EB),
        lightSecondary = Color(0xFF0D9488),
        darkPrimary = Color(0xFF38BDF8),
        darkSecondary = Color(0xFF2DD4BF),
        lightContainer = Color(0xFFDBEAFE),
        darkContainer = Color(0xFF1E3A5F)
    ),
    EMERALD_GREEN(
        title = "سبز تأسیساتی",
        description = "طراوت و انرژی تأسیسات",
        previewColor = Color(0xFF059669),
        lightPrimary = Color(0xFF059669),
        lightSecondary = Color(0xFF10B981),
        darkPrimary = Color(0xFF34D399),
        darkSecondary = Color(0xFF6EE7B7),
        lightContainer = Color(0xFFD1FAE5),
        darkContainer = Color(0xFF064E3B)
    ),
    COPPER_AMBER(
        title = "مسی لوله‌کشی",
        description = "اصالت لوله‌های مسی و گرما",
        previewColor = Color(0xFFD97706),
        lightPrimary = Color(0xFFD97706),
        lightSecondary = Color(0xFFEA580C),
        darkPrimary = Color(0xFFFBBF24),
        darkSecondary = Color(0xFFFB923C),
        lightContainer = Color(0xFFFEF3C7),
        darkContainer = Color(0xFF78350F)
    ),
    STEEL_NAVY(
        title = "سرمه‌ای صنعتی",
        description = "صلابت و دقت مهندسی",
        previewColor = Color(0xFF1E3A8A),
        lightPrimary = Color(0xFF1E3A8A),
        lightSecondary = Color(0xFF3B82F6),
        darkPrimary = Color(0xFF60A5FA),
        darkSecondary = Color(0xFF93C5FD),
        lightContainer = Color(0xFFE0E7FF),
        darkContainer = Color(0xFF1E293B)
    ),
    RUBY_CRIMSON(
        title = "یاقوتی زرشکی",
        description = "جذابیت و پویایی بالا",
        previewColor = Color(0xFFE11D48),
        lightPrimary = Color(0xFFE11D48),
        lightSecondary = Color(0xFFBE123C),
        darkPrimary = Color(0xFFFB7185),
        darkSecondary = Color(0xFFFDA4AF),
        lightContainer = Color(0xFFFFE4E6),
        darkContainer = Color(0xFF881337)
    ),
    ROYAL_VIOLET(
        title = "بنفش سلطنتی",
        description = "لوکس و مدرن",
        previewColor = Color(0xFF7C3AED),
        lightPrimary = Color(0xFF7C3AED),
        lightSecondary = Color(0xFF9333EA),
        darkPrimary = Color(0xFFC084FC),
        darkSecondary = Color(0xFFE9D5FF),
        lightContainer = Color(0xFFEDE9FE),
        darkContainer = Color(0xFF581C87)
    )
}
