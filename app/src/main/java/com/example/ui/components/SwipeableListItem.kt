package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.LocalGlassTokens
import kotlin.math.abs

@Composable
fun SwipeableListItem(
    modifier: Modifier = Modifier,
    isOpen: Boolean = false,
    onOpenChange: ((Boolean) -> Unit)? = null,
    onEdit: (() -> Unit)? = null,
    onDelete: () -> Unit,
    deleteConfirmTitle: String = "حذف آیتم",
    deleteConfirmMessage: String = "آیا از حذف این مورد اطمینان دارید؟",
    content: @Composable () -> Unit
) {
    val tokens = LocalGlassTokens.current
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var totalDragAmount by remember { mutableFloatStateOf(0f) }

    ConfirmDialog(
        show = showDeleteConfirm,
        title = deleteConfirmTitle,
        message = deleteConfirmMessage,
        confirmText = "حذف",
        cancelText = "انصراف",
        onConfirm = {
            onOpenChange?.invoke(false)
            onDelete()
        },
        onDismiss = {
            showDeleteConfirm = false
            onOpenChange?.invoke(false)
        }
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(isOpen) {
                detectHorizontalDragGestures(
                    onDragStart = { totalDragAmount = 0f },
                    onDragEnd = {
                        // In RTL, dragging to the left (negative drag) opens actions; dragging right closes them.
                        // We also support pulling in either direction!
                        if (abs(totalDragAmount) > 40f) {
                            if (totalDragAmount < -40f) {
                                onOpenChange?.invoke(true)
                            } else if (totalDragAmount > 40f) {
                                // If already open, drag right closes; if closed, drag right can also open
                                if (isOpen) {
                                    onOpenChange?.invoke(false)
                                } else {
                                    onOpenChange?.invoke(true)
                                }
                            }
                        }
                        totalDragAmount = 0f
                    },
                    onDragCancel = { totalDragAmount = 0f },
                    onHorizontalDrag = { _, dragAmount ->
                        totalDragAmount += dragAmount
                    }
                )
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Main Card Content: Takes full width when closed, smoothly adjusts when open
        Box(
            modifier = Modifier
                .weight(1f)
                .pointerInput(isOpen) {
                    detectTapGestures(
                        onLongPress = {
                            onOpenChange?.invoke(!isOpen)
                        },
                        onTap = {
                            if (isOpen) {
                                onOpenChange?.invoke(false)
                            }
                        }
                    )
                }
        ) {
            content()
        }

        // Action Buttons: Expanded smoothly BESIDE the card in the Row!
        // This guarantees the buttons are NEVER underneath or obscured by the card.
        AnimatedVisibility(
            visible = isOpen,
            enter = expandHorizontally(
                animationSpec = spring(
                    stiffness = Spring.StiffnessMediumLow,
                    dampingRatio = Spring.DampingRatioNoBouncy
                )
            ) + fadeIn(),
            exit = shrinkHorizontally(
                animationSpec = spring(
                    stiffness = Spring.StiffnessMediumLow,
                    dampingRatio = Spring.DampingRatioNoBouncy
                )
            ) + fadeOut()
        ) {
            Row(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .testTag("swipe_actions_container"),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onEdit != null) {
                    GlassActionButton(
                        icon = Icons.Default.Edit,
                        title = "ویرایش",
                        backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(
                            alpha = if (tokens.isDark) 0.65f else 0.85f
                        ),
                        contentColor = MaterialTheme.colorScheme.primary,
                        borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        onClick = {
                            onOpenChange?.invoke(false)
                            onEdit()
                        },
                        testTag = "swipe_edit_action"
                    )
                }

                GlassActionButton(
                    icon = Icons.Default.Delete,
                    title = "حذف",
                    backgroundColor = ErrorRed.copy(
                        alpha = if (tokens.isDark) 0.3f else 0.2f
                    ),
                    contentColor = ErrorRed,
                    borderColor = ErrorRed.copy(alpha = 0.6f),
                    onClick = {
                        showDeleteConfirm = true
                    },
                    testTag = "swipe_delete_action"
                )
            }
        }
    }
}

@Composable
private fun GlassActionButton(
    icon: ImageVector,
    title: String,
    backgroundColor: Color,
    contentColor: Color,
    borderColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = Modifier
            .width(62.dp)
            .height(60.dp)
            .shadow(4.dp, shape)
            .clip(shape)
            .background(backgroundColor)
            .border(BorderStroke(1.2.dp, borderColor), shape)
            .clickable(onClick = onClick)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = contentColor
            )
        }
    }
}
