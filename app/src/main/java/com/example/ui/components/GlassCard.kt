package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalGlassTokens

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    borderWidth: Dp = 1.dp,
    elevation: Dp = 6.dp,
    testTag: String? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val tokens = LocalGlassTokens.current

    val baseModifier = modifier
        .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
        .shadow(
            elevation = elevation,
            shape = shape,
            ambientColor = if (tokens.isDark) Color.Black.copy(alpha = 0.5f) else Color(0x20000000),
            spotColor = if (tokens.isDark) Color.Black.copy(alpha = 0.5f) else Color(0x30000000)
        )
        .clip(shape)
        .background(tokens.cardGradient)
        .border(
            border = BorderStroke(borderWidth, tokens.glassBorder),
            shape = shape
        )
        .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)

    Box(
        modifier = baseModifier,
        content = content
    )
}

@Composable
fun GlassBadge(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
    contentColor: Color = MaterialTheme.colorScheme.primary
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        border = BorderStroke(1.dp, contentColor.copy(alpha = 0.3f))
    ) {
        androidx.compose.material3.Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
