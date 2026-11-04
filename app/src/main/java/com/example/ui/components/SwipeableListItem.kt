package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.LocalGlassTokens
import kotlin.math.abs
import kotlin.math.roundToInt

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
    val density = LocalDensity.current
    val tokens = LocalGlassTokens.current

    // Calculate action width: ~70dp per action button + spacing
    val totalButtons = if (onEdit != null) 2 else 1
    val actionWidthDp = if (totalButtons == 2) 156.dp else 84.dp
    val actionWidthPx = with(density) { actionWidthDp.toPx() }
    val dragThresholdPx = actionWidthPx * 0.35f

    var localOffsetX by remember { mutableFloatStateOf(0f) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    // Synchronize external isOpen state (e.g. closing when another item opens)
    LaunchedEffect(isOpen) {
        if (!isOpen && localOffsetX != 0f) {
            localOffsetX = 0f
        }
    }

    val animatedOffset by animateFloatAsState(
        targetValue = localOffsetX,
        animationSpec = spring(
            stiffness = Spring.StiffnessMediumLow,
            dampingRatio = Spring.DampingRatioNoBouncy
        ),
        label = "swipe_offset"
    )

    fun closeActions() {
        localOffsetX = 0f
        onOpenChange?.invoke(false)
    }

    ConfirmDialog(
        show = showDeleteConfirm,
        title = deleteConfirmTitle,
        message = deleteConfirmMessage,
        confirmText = "حذف",
        cancelText = "انصراف",
        onConfirm = {
            closeActions()
            onDelete()
        },
        onDismiss = {
            showDeleteConfirm = false
            closeActions()
        }
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
    ) {
        // Actions Layer: ONLY rendered and visible when swiped!
        // When resting (animatedOffset close to 0), this is completely hidden to prevent ghosting through glass cards.
        val progress = (abs(animatedOffset) / actionWidthPx).coerceIn(0f, 1f)
        if (abs(animatedOffset) > 1f) {
            // In RTL:
            // animatedOffset < 0 (slid left) -> empty space is on Right -> Alignment.CenterStart
            // animatedOffset > 0 (slid right) -> empty space is on Left -> Alignment.CenterEnd
            val alignment = if (animatedOffset < 0) Alignment.CenterStart else Alignment.CenterEnd

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(horizontal = 6.dp),
                contentAlignment = alignment
            ) {
                Row(
                    modifier = Modifier
                        .alpha(progress)
                        .scale(0.85f + 0.15f * progress)
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (onEdit != null) {
                        GlassActionButton(
                            icon = Icons.Default.Edit,
                            title = "ویرایش",
                            backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(
                                alpha = if (tokens.isDark) 0.6f else 0.85f
                            ),
                            contentColor = MaterialTheme.colorScheme.primary,
                            borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            onClick = {
                                closeActions()
                                onEdit()
                            },
                            testTag = "swipe_edit_action"
                        )
                    }

                    GlassActionButton(
                        icon = Icons.Default.Delete,
                        title = "حذف",
                        backgroundColor = ErrorRed.copy(
                            alpha = if (tokens.isDark) 0.25f else 0.15f
                        ),
                        contentColor = ErrorRed,
                        borderColor = ErrorRed.copy(alpha = 0.5f),
                        onClick = {
                            showDeleteConfirm = true
                        },
                        testTag = "swipe_delete_action"
                    )
                }
            }
        }

        // Foreground Card Layer
        Box(
            modifier = Modifier
                .offset { IntOffset(animatedOffset.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (localOffsetX < -dragThresholdPx) {
                                localOffsetX = -actionWidthPx
                                onOpenChange?.invoke(true)
                            } else if (localOffsetX > dragThresholdPx) {
                                localOffsetX = actionWidthPx
                                onOpenChange?.invoke(true)
                            } else {
                                localOffsetX = 0f
                                onOpenChange?.invoke(false)
                            }
                        },
                        onDragCancel = {
                            localOffsetX = 0f
                            onOpenChange?.invoke(false)
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            val newOffset = localOffsetX + dragAmount
                            localOffsetX = newOffset.coerceIn(-actionWidthPx, actionWidthPx)
                        }
                    )
                }
        ) {
            content()

            // Dismiss overlay: tapping card while actions are revealed closes them
            if (abs(animatedOffset) > 10f) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { closeActions() }
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
            .width(68.dp)
            .height(64.dp)
            .shadow(4.dp, shape)
            .clip(shape)
            .background(backgroundColor)
            .border(BorderStroke(1.dp, borderColor), shape)
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
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(3.dp))
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
