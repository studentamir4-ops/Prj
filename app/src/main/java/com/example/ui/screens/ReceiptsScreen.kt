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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ReceiptEntity
import com.example.ui.components.GlassCard
import com.example.ui.components.LiveThousandSeparatorField
import com.example.ui.components.SwipeableListItem
import com.example.ui.theme.LocalGlassTokens
import com.example.ui.theme.SuccessGreen
import com.example.util.NumberFormatUtils
import com.example.util.PersianDateUtils
import com.example.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val receipts by viewModel.receipts.collectAsState()
    val projects by viewModel.projects.collectAsState()
    val tokens = LocalGlassTokens.current

    val projectMap = remember(projects) { projects.associateBy { it.id } }

    var showDialog by remember { mutableStateOf(false) }
    var editingReceipt by remember { mutableStateOf<ReceiptEntity?>(null) }

    var amountInput by remember { mutableStateOf("") }
    var dateInput by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("کارت به کارت") }
    var description by remember { mutableStateOf("") }
    var selectedProjectId by remember { mutableStateOf<Long?>(null) }

    var projectDropdownExpanded by remember { mutableStateOf(false) }
    var paymentMethodExpanded by remember { mutableStateOf(false) }

    val paymentMethods = listOf("کارت به کارت", "انتقال پایا", "ساتنا", "نقدی", "چک صیادی", "واریز به حساب")

    fun openAddDialog() {
        editingReceipt = null
        amountInput = ""
        dateInput = PersianDateUtils.getTodayJalali().formatToString()
        paymentMethod = "کارت به کارت"
        description = ""
        selectedProjectId = null
        showDialog = true
    }

    fun openEditDialog(receipt: ReceiptEntity) {
        editingReceipt = receipt
        amountInput = receipt.amount.toString()
        dateInput = receipt.date
        paymentMethod = receipt.paymentMethod
        description = receipt.description
        selectedProjectId = receipt.projectId
        showDialog = true
    }

    if (showDialog) {
        Dialog(
            onDismissRequest = { showDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .padding(16.dp)
                    .testTag("receipt_form_dialog"),
                shape = RoundedCornerShape(24.dp),
                elevation = 16.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = if (editingReceipt == null) "ثبت دریافتی جدید" else "ویرایش دریافتی",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Amount
                    LiveThousandSeparatorField(
                        value = amountInput,
                        onValueChange = { amountInput = it },
                        label = "مبلغ دریافتی *",
                        testTag = "receipt_amount_input"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Date
                    OutlinedTextField(
                        value = dateInput,
                        onValueChange = { dateInput = it },
                        label = { Text("تاریخ دریافت (شمسی) *") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("receipt_date_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = tokens.glassBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Payment Method Dropdown
                    ExposedDropdownMenuBox(
                        expanded = paymentMethodExpanded,
                        onExpandedChange = { paymentMethodExpanded = !paymentMethodExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = paymentMethod,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("روش دریافت") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = paymentMethodExpanded) },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("receipt_method_selector"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = tokens.glassBorder
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = paymentMethodExpanded,
                            onDismissRequest = { paymentMethodExpanded = false }
                        ) {
                            paymentMethods.forEach { method ->
                                DropdownMenuItem(
                                    text = { Text(method) },
                                    onClick = {
                                        paymentMethod = method
                                        paymentMethodExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Optional Project Connection
                    ExposedDropdownMenuBox(
                        expanded = projectDropdownExpanded,
                        onExpandedChange = { projectDropdownExpanded = !projectDropdownExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val currentProjectTitle = selectedProjectId?.let { projectMap[it]?.title } ?: "بدون اتصال به پروژه (دریافتی مستقل)"
                        OutlinedTextField(
                            value = currentProjectTitle,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("پروژه مرتبط (اختیاری)") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = projectDropdownExpanded) },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("receipt_project_selector"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = tokens.glassBorder
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = projectDropdownExpanded,
                            onDismissRequest = { projectDropdownExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("بدون اتصال به پروژه (مستقل)") },
                                onClick = {
                                    selectedProjectId = null
                                    projectDropdownExpanded = false
                                }
                            )
                            projects.forEach { p ->
                                DropdownMenuItem(
                                    text = { Text(p.title) },
                                    onClick = {
                                        selectedProjectId = p.id
                                        projectDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Description
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("توضیحات و بابت") },
                        maxLines = 3,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("receipt_desc_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = tokens.glassBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = { showDialog = false },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("cancel_receipt_button")
                        ) {
                            Text("انصراف")
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        val amt = NumberFormatUtils.parseAmount(amountInput)
                        Button(
                            onClick = {
                                if (amt > 0) {
                                    val current = editingReceipt
                                    if (current == null) {
                                        viewModel.addReceipt(
                                            amount = amt,
                                            date = dateInput,
                                            paymentMethod = paymentMethod,
                                            description = description,
                                            projectId = selectedProjectId
                                        )
                                    } else {
                                        viewModel.updateReceipt(
                                            current.copy(
                                                amount = amt,
                                                date = dateInput,
                                                paymentMethod = paymentMethod,
                                                description = description.trim(),
                                                projectId = selectedProjectId
                                            )
                                        )
                                    }
                                    showDialog = false
                                }
                            },
                            enabled = amt > 0,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("save_receipt_button")
                        ) {
                            Text(if (editingReceipt == null) "ثبت دریافتی" else "ذخیره تغییرات")
                        }
                    }
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("receipts_list"),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            item {
                Column {
                    Text(
                        text = "مدیریت دریافتی‌ها",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "ثبت مستقل تمام واریزی‌ها و تسویه‌حساب‌ها",
                        style = MaterialTheme.typography.bodyMedium,
                        color = tokens.textMuted
                    )
                }
            }

            if (receipts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "هیچ دریافتی‌ای ثبت نشده است",
                            style = MaterialTheme.typography.bodyMedium,
                            color = tokens.textMuted
                        )
                    }
                }
            } else {
                items(receipts, key = { it.id }) { receipt ->
                    val projTitle = receipt.projectId?.let { projectMap[it]?.title }

                    SwipeableListItem(
                        onEdit = { openEditDialog(receipt) },
                        onDelete = { viewModel.deleteReceipt(receipt) },
                        deleteConfirmTitle = "حذف دریافتی",
                        deleteConfirmMessage = "آیا از حذف این دریافتی به مبلغ ${NumberFormatUtils.formatToman(receipt.amount)} اطمینان دارید؟"
                    ) {
                        ReceiptCardItem(
                            receipt = receipt,
                            projectTitle = projTitle
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { openAddDialog() },
            containerColor = SuccessGreen,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
                .testTag("add_receipt_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "ثبت دریافتی جدید")
        }
    }
}

@Composable
private fun ReceiptCardItem(
    receipt: ReceiptEntity,
    projectTitle: String?
) {
    val tokens = LocalGlassTokens.current

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = 3.dp
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
                    text = NumberFormatUtils.formatToman(receipt.amount),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = SuccessGreen
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "تاریخ",
                        tint = tokens.textMuted,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = receipt.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.textMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CreditCard,
                    contentDescription = "روش",
                    tint = tokens.textMuted,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "روش: ${receipt.paymentMethod}",
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.textMuted
                )
            }

            if (!projectTitle.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.WorkOutline,
                        contentDescription = "پروژه",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "پروژه: $projectTitle",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (receipt.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = receipt.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.textMuted.copy(alpha = 0.8f)
                )
            }
        }
    }
}
