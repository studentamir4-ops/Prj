package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.PriceCheck
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ProjectEntity
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassCard
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.InfoCyan
import com.example.ui.theme.LocalGlassTokens
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber
import com.example.util.NumberFormatUtils
import com.example.viewmodel.DashboardFilter
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.ProjectShareItem
import com.example.viewmodel.ReceiptDetailItem

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onNavigateToProjects: () -> Unit,
    onNavigateToReceipts: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.dashboardStats.collectAsState()
    val currentFilter by viewModel.selectedFilter.collectAsState()
    val showMyShareModal by viewModel.showMyShareModal.collectAsState()
    val showTotalReceivedModal by viewModel.showTotalReceivedModal.collectAsState()
    val projectShareItems by viewModel.projectShareItems.collectAsState()
    val receiptDetailItems by viewModel.receiptDetailItems.collectAsState()
    val tokens = LocalGlassTokens.current

    // Modal: My Share Details
    if (showMyShareModal) {
        Dialog(
            onDismissRequest = { viewModel.closeMyShareModal() },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .heightIn(max = 560.dp)
                    .padding(16.dp)
                    .testTag("my_share_modal"),
                shape = RoundedCornerShape(24.dp),
                elevation = 16.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    // Header with close
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "جزئیات سهم من از پروژه‌ها",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        IconButton(
                            onClick = { viewModel.closeMyShareModal() },
                            modifier = Modifier.testTag("close_my_share_modal")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "بستن")
                        }
                    }

                    // Total Share Banner
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        elevation = 4.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "مجموع کل سهم من",
                                style = MaterialTheme.typography.bodySmall,
                                color = tokens.textMuted
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = NumberFormatUtils.formatToman(stats.myShareTotal),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Projects list
                    if (projectShareItems.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "هیچ پروژه‌ای در این بازه زمانی یافت نشد",
                                style = MaterialTheme.typography.bodyMedium,
                                color = tokens.textMuted
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(projectShareItems) { item ->
                                ProjectShareRow(item = item)
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal: Total Received Details
    if (showTotalReceivedModal) {
        Dialog(
            onDismissRequest = { viewModel.closeTotalReceivedModal() },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .heightIn(max = 560.dp)
                    .padding(16.dp)
                    .testTag("total_received_modal"),
                shape = RoundedCornerShape(24.dp),
                elevation = 16.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "جزئیات مبالغ دریافتی",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        IconButton(
                            onClick = { viewModel.closeTotalReceivedModal() },
                            modifier = Modifier.testTag("close_total_received_modal")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "بستن")
                        }
                    }

                    // Total Received Banner
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        elevation = 4.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "مجموع دریافتی‌های ثبت‌شده",
                                style = MaterialTheme.typography.bodySmall,
                                color = tokens.textMuted
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = NumberFormatUtils.formatToman(stats.totalReceived),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                        }
                    }

                    // Receipts List
                    if (receiptDetailItems.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "هیچ دریافتی‌ای در این بازه زمانی ثبت نشده است",
                                style = MaterialTheme.typography.bodyMedium,
                                color = tokens.textMuted
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(receiptDetailItems) { item ->
                                ReceiptDetailRow(item = item)
                            }
                        }
                    }
                }
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Top Header
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                Text(
                    text = "نمای کلی عملکرد و مالی",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "گزارش وضعیت پروژه‌ها، درآمد و تعهدات کاری",
                    style = MaterialTheme.typography.bodyMedium,
                    color = tokens.textMuted
                )
            }
        }

        // Time Filters Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DashboardFilter.values().forEach { filter ->
                    val isSelected = (filter == currentFilter)
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setFilter(filter) },
                        label = {
                            Text(
                                text = filter.title,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White,
                            containerColor = tokens.glassSurfaceVariant.copy(alpha = 0.4f),
                            labelColor = MaterialTheme.colorScheme.onSurface
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = tokens.glassBorder,
                            selectedBorderColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("filter_chip_${filter.name}")
                    )
                }
            }
        }

        // Primary Financial Metrics (سهم من & مجموع دریافتی)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. سهم من (Clickable -> Opens Modal)
                DashboardMetricCard(
                    title = "سهم من",
                    amount = stats.myShareTotal,
                    subtitle = "مجموع سهم حاصل از پروژه‌ها • برای مشاهده جزئیات لمس کنید",
                    icon = Icons.Default.Savings,
                    accentColor = MaterialTheme.colorScheme.primary,
                    testTag = "card_my_share",
                    onClick = { viewModel.openMyShareModal() }
                )

                // 2. مجموع دریافتی (Clickable -> Opens Modal)
                DashboardMetricCard(
                    title = "مجموع دریافتی",
                    amount = stats.totalReceived,
                    subtitle = "مجموع تمام مبالغ دریافت شده • برای مشاهده جزئیات لمس کنید",
                    icon = Icons.Default.PriceCheck,
                    accentColor = SuccessGreen,
                    testTag = "card_total_received",
                    onClick = { viewModel.openTotalReceivedModal() }
                )

                // 3. مطالبات (سهم من - دریافتی)
                val isNegative = stats.outstandingReceivables < 0
                val displayReceivables = if (isNegative) 0L else stats.outstandingReceivables
                DashboardMetricCard(
                    title = "مطالبات باقیمانده",
                    amount = displayReceivables,
                    subtitle = if (isNegative) "دریافتی بیش از سهم محاسبه شده است" else "سهم منهای دریافتی‌ها",
                    icon = Icons.Default.AccountBalanceWallet,
                    accentColor = if (stats.outstandingReceivables > 0) WarningAmber else SuccessGreen,
                    testTag = "card_receivables"
                )
            }
        }

        // Project Status & Workday Grid (4 Cards)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "آمار پروژه‌ها و تقویم کاری",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 4. کل پروژه‌ها
                    SmallStatCard(
                        title = "تعداد کل پروژه‌ها",
                        count = stats.totalProjectsCount.toString(),
                        unit = "پروژه",
                        icon = Icons.Default.Assignment,
                        accentColor = InfoCyan,
                        modifier = Modifier.weight(1f),
                        testTag = "card_total_projects"
                    )

                    // 5. پروژه‌های در حال انجام
                    SmallStatCard(
                        title = "در حال انجام",
                        count = stats.inProgressProjectsCount.toString(),
                        unit = "پروژه",
                        icon = Icons.Default.PendingActions,
                        accentColor = WarningAmber,
                        modifier = Modifier.weight(1f),
                        testTag = "card_in_progress_projects"
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 6. پروژه‌های تکمیل‌شده
                    SmallStatCard(
                        title = "تکمیل‌شده",
                        count = stats.completedProjectsCount.toString(),
                        unit = "پروژه",
                        icon = Icons.Default.CheckCircle,
                        accentColor = SuccessGreen,
                        modifier = Modifier.weight(1f),
                        testTag = "card_completed_projects"
                    )

                    // 7. تعداد روزهای کاری
                    SmallStatCard(
                        title = "تعداد روزهای کاری",
                        count = stats.workdaysCount.toString(),
                        unit = "روز",
                        icon = Icons.Default.CalendarMonth,
                        accentColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f),
                        testTag = "card_workdays_count"
                    )
                }
            }
        }
    }
}

@Composable
private fun DashboardMetricCard(
    title: String,
    amount: Long,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    testTag: String,
    onClick: (() -> Unit)? = null
) {
    val tokens = LocalGlassTokens.current

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        testTag = testTag,
        onClick = onClick,
        elevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = tokens.textMuted
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = NumberFormatUtils.formatToman(amount),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.textMuted.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun SmallStatCard(
    title: String,
    count: String,
    unit: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    val tokens = LocalGlassTokens.current

    GlassCard(
        modifier = modifier,
        testTag = testTag,
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.textMuted,
                    maxLines = 1
                )
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = count,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = unit,
                    style = MaterialTheme.typography.labelMedium,
                    color = tokens.textMuted,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun ProjectShareRow(item: ProjectShareItem) {
    val tokens = LocalGlassTokens.current
    val isCompleted = item.status == ProjectEntity.STATUS_COMPLETED

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.projectTitle,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                GlassBadge(
                    text = if (isCompleted) "تکمیل شده" else "در حال انجام",
                    backgroundColor = if (isCompleted) SuccessGreen.copy(alpha = 0.15f) else WarningAmber.copy(alpha = 0.15f),
                    contentColor = if (isCompleted) SuccessGreen else WarningAmber
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "کارفرما: ${item.clientName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.textMuted
                )
                Text(
                    text = "سهم من: ${NumberFormatUtils.formatToman(item.myShare)}",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ReceiptDetailRow(item: ReceiptDetailItem) {
    val tokens = LocalGlassTokens.current

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = NumberFormatUtils.formatToman(item.amount),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SuccessGreen
                )
                Text(
                    text = item.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.textMuted
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "روش: ${item.paymentMethod}",
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.textMuted
                )
                if (!item.projectTitle.isNullOrEmpty()) {
                    Text(
                        text = "پروژه: ${item.projectTitle}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (item.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.textMuted.copy(alpha = 0.9f)
                )
            }
        }
    }
}
