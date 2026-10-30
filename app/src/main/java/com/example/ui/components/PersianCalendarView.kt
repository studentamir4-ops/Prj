package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WorkdayEntity
import com.example.ui.theme.LocalGlassTokens
import com.example.ui.theme.SuccessGreen
import com.example.util.JalaliDate
import com.example.util.PersianDateUtils

@Composable
fun PersianCalendarView(
    currentYear: Int,
    currentMonth: Int,
    workdays: List<WorkdayEntity>,
    onMonthChange: (year: Int, month: Int) -> Unit,
    onDayClick: (year: Int, month: Int, day: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalGlassTokens.current
    val today = remember { PersianDateUtils.getTodayJalali() }

    // Helper to advance or retreat month
    fun goToNextMonth() {
        if (currentMonth == 12) {
            onMonthChange(currentYear + 1, 1)
        } else {
            onMonthChange(currentYear, currentMonth + 1)
        }
    }

    fun goToPrevMonth() {
        if (currentMonth == 1) {
            onMonthChange(currentYear - 1, 12)
        } else {
            onMonthChange(currentYear, currentMonth - 1)
        }
    }

    // Direction tracking for animation: 1 = next month, -1 = prev month
    var slideDirection by remember { mutableIntStateOf(1) }
    var accumulatedDrag by remember { mutableFloatStateOf(0f) }

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(currentYear, currentMonth) {
                detectHorizontalDragGestures(
                    onDragStart = { accumulatedDrag = 0f },
                    onDragEnd = {
                        // Independent of RTL:
                        // Swipe left-to-right (drag > +60px) -> Next Month
                        // Swipe right-to-left (drag < -60px) -> Prev Month
                        if (accumulatedDrag > 60f) {
                            slideDirection = 1
                            goToNextMonth()
                        } else if (accumulatedDrag < -60f) {
                            slideDirection = -1
                            goToPrevMonth()
                        }
                        accumulatedDrag = 0f
                    },
                    onDragCancel = { accumulatedDrag = 0f },
                    onHorizontalDrag = { _, dragAmount ->
                        accumulatedDrag += dragAmount
                    }
                )
            }
            .testTag("persian_calendar_view"),
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Month & Year Selector with smooth controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        slideDirection = -1
                        goToPrevMonth()
                    },
                    modifier = Modifier.testTag("calendar_prev_month")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "ماه قبل",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${PersianDateUtils.getMonthName(currentMonth)} $currentYear",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "برای ثبت/حذف روز کاری روی تاریخ ضربه بزنید",
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.textMuted
                    )
                }

                IconButton(
                    onClick = {
                        slideDirection = 1
                        goToNextMonth()
                    },
                    modifier = Modifier.testTag("calendar_next_month")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "ماه بعد",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Weekday Header (شنبه تا جمعه)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                PersianDateUtils.weekDayNames.forEachIndexed { index, name ->
                    val isFriday = (index == 6)
                    Text(
                        text = name,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isFriday) MaterialTheme.colorScheme.error else tokens.textMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Days Grid with animated transition
            val monthKey = "$currentYear-$currentMonth"
            AnimatedContent(
                targetState = monthKey,
                transitionSpec = {
                    if (slideDirection > 0) {
                        (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> -width } + fadeOut()
                        )
                    } else {
                        (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> width } + fadeOut()
                        )
                    }
                },
                label = "month_transition"
            ) { _ ->
                val daysInMonth = PersianDateUtils.getDaysInMonth(currentYear, currentMonth)
                val startDayOfWeek = PersianDateUtils.getFirstDayOfWeekInMonth(currentYear, currentMonth)
                val totalSlots = ((startDayOfWeek + daysInMonth + 6) / 7) * 7

                val recordedDaySet = remember(workdays, currentYear, currentMonth) {
                    workdays.map { it.day }.toSet()
                }

                Column(modifier = Modifier.fillMaxWidth()) {
                    val rows = totalSlots / 7
                    for (row in 0 until rows) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            for (col in 0 until 7) {
                                val slotIndex = row * 7 + col
                                val dayNumber = slotIndex - startDayOfWeek + 1

                                if (dayNumber in 1..daysInMonth) {
                                    val isWorkday = recordedDaySet.contains(dayNumber)
                                    val isToday = (today.year == currentYear && today.month == currentMonth && today.day == dayNumber)
                                    val isFriday = (col == 6)

                                    DayCell(
                                        dayNumber = dayNumber,
                                        isWorkday = isWorkday,
                                        isToday = isToday,
                                        isFriday = isFriday,
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            onDayClick(currentYear, currentMonth, dayNumber)
                                        }
                                    )
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    dayNumber: Int,
    isWorkday: Boolean,
    isToday: Boolean,
    isFriday: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalGlassTokens.current

    val backgroundModifier = when {
        isWorkday -> Modifier
            .background(SuccessGreen, CircleShape)
        isToday -> Modifier
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape)
            .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
        else -> Modifier
    }

    val textColor = when {
        isWorkday -> Color.White
        isToday -> MaterialTheme.colorScheme.primary
        isFriday -> MaterialTheme.colorScheme.error.copy(alpha = 0.9f)
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .then(backgroundModifier)
            .clickable(onClick = onClick)
            .testTag("calendar_day_$dayNumber"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = dayNumber.toString(),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 13.sp,
                fontWeight = if (isWorkday || isToday) FontWeight.Bold else FontWeight.Normal
            ),
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}
