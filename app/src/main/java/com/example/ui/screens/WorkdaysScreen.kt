package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.WorkdayEntity
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassCard
import com.example.ui.components.PersianCalendarView
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.LocalGlassTokens
import com.example.ui.theme.SuccessGreen
import com.example.util.PersianDateUtils
import com.example.viewmodel.MainViewModel

@Composable
fun WorkdaysScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val workdays by viewModel.workdays.collectAsState()
    val calendarYear by viewModel.calendarYear.collectAsState()
    val calendarMonth by viewModel.calendarMonth.collectAsState()
    val tokens = LocalGlassTokens.current

    // Filter workdays for the currently viewed month
    val currentMonthWorkdays = remember(workdays, calendarYear, calendarMonth) {
        workdays.filter { it.year == calendarYear && it.month == calendarMonth }
            .sortedBy { it.day }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("workdays_screen"),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Screen Header
        item {
            Column {
                Text(
                    text = "مدیریت روزهای کاری",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "تقویم شمسی • برای ورق زدن ماه‌ها به طرفین Swipe کنید",
                    style = MaterialTheme.typography.bodyMedium,
                    color = tokens.textMuted
                )
            }
        }

        // Solar Calendar View
        item {
            PersianCalendarView(
                currentYear = calendarYear,
                currentMonth = calendarMonth,
                workdays = currentMonthWorkdays,
                onMonthChange = { y, m -> viewModel.setCalendarMonth(y, m) },
                onDayClick = { y, m, d -> viewModel.toggleWorkday(y, m, d) }
            )
        }

        // Summary Card for this month
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "روزهای کاری ${PersianDateUtils.getMonthName(calendarMonth)} $calendarYear",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "مجموع ثبت شده در این ماه",
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.textMuted
                        )
                    }

                    GlassBadge(
                        text = "${currentMonthWorkdays.size} روز",
                        backgroundColor = SuccessGreen.copy(alpha = 0.15f),
                        contentColor = SuccessGreen
                    )
                }
            }
        }

        // List of Recorded Workdays for this month
        item {
            Text(
                text = "فهرست روزهای کاری ثبت‌شده این ماه:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        if (currentMonthWorkdays.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "هنوز هیچ روز کاری برای این ماه ثبت نشده است",
                        style = MaterialTheme.typography.bodyMedium,
                        color = tokens.textMuted
                    )
                }
            }
        } else {
            items(currentMonthWorkdays, key = { it.id }) { workday ->
                WorkdayRowItem(
                    workday = workday,
                    onDelete = { viewModel.toggleWorkday(workday.year, workday.month, workday.day) }
                )
            }
        }
    }
}

@Composable
private fun WorkdayRowItem(
    workday: WorkdayEntity,
    onDelete: () -> Unit
) {
    val tokens = LocalGlassTokens.current
    val dayOfWeekName = remember(workday) {
        val gCal = PersianDateUtils.jalaliToGregorian(workday.year, workday.month, workday.day)
        val dowIndex = when (gCal.get(java.util.Calendar.DAY_OF_WEEK)) {
            java.util.Calendar.SATURDAY -> 0
            java.util.Calendar.SUNDAY -> 1
            java.util.Calendar.MONDAY -> 2
            java.util.Calendar.TUESDAY -> 3
            java.util.Calendar.WEDNESDAY -> 4
            java.util.Calendar.THURSDAY -> 5
            java.util.Calendar.FRIDAY -> 6
            else -> 0
        }
        PersianDateUtils.weekDayFullNames[dowIndex]
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SuccessGreen.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "ثبت شده",
                        tint = SuccessGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "$dayOfWeekName ${workday.day} ${PersianDateUtils.getMonthName(workday.month)} ${workday.year}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = workday.solarDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.textMuted
                    )
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.testTag("delete_workday_${workday.day}")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "حذف این روز کاری",
                    tint = ErrorRed.copy(alpha = 0.8f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
