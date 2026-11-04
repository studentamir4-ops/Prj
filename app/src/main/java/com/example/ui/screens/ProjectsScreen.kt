package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
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
import com.example.data.model.ClientEntity
import com.example.data.model.ProjectEntity
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassCard
import com.example.ui.components.LiveThousandSeparatorField
import com.example.ui.components.SwipeableListItem
import com.example.ui.theme.LocalGlassTokens
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber
import com.example.util.NumberFormatUtils
import com.example.util.PersianDateUtils
import com.example.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val projects by viewModel.projects.collectAsState()
    val clients by viewModel.clients.collectAsState()
    val tokens = LocalGlassTokens.current

    var selectedStatusFilter by remember { mutableStateOf("ALL") }
    var openProjectId by remember { mutableStateOf<Long?>(null) }

    // Dialog state for add/edit project
    var showDialog by remember { mutableStateOf(false) }
    var editingProject by remember { mutableStateOf<ProjectEntity?>(null) }

    var title by remember { mutableStateOf("") }
    var selectedClientId by remember { mutableStateOf<Long?>(null) }
    var totalAmountInput by remember { mutableStateOf("") }
    var myShareType by remember { mutableStateOf(ProjectEntity.SHARE_TYPE_PERCENTAGE) }
    var customAmountInput by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(ProjectEntity.STATUS_IN_PROGRESS) }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var clientDropdownExpanded by remember { mutableStateOf(false) }

    val clientMap = remember(clients) { clients.associateBy { it.id } }

    val filteredProjects = remember(projects, selectedStatusFilter) {
        when (selectedStatusFilter) {
            "IN_PROGRESS" -> projects.filter { it.status == ProjectEntity.STATUS_IN_PROGRESS }
            "COMPLETED" -> projects.filter { it.status == ProjectEntity.STATUS_COMPLETED }
            else -> projects
        }
    }

    fun openAddDialog() {
        editingProject = null
        title = ""
        selectedClientId = clients.firstOrNull()?.id
        totalAmountInput = ""
        myShareType = ProjectEntity.SHARE_TYPE_PERCENTAGE
        customAmountInput = ""
        description = ""
        status = ProjectEntity.STATUS_IN_PROGRESS
        startDate = PersianDateUtils.getTodayJalali().formatToString()
        endDate = ""
        showDialog = true
    }

    fun openEditDialog(proj: ProjectEntity) {
        editingProject = proj
        title = proj.title
        selectedClientId = proj.clientId
        totalAmountInput = proj.totalAmount.toString()
        myShareType = proj.myShareType
        customAmountInput = if (proj.customAmount > 0) proj.customAmount.toString() else ""
        description = proj.description
        status = proj.status
        startDate = proj.startDate
        endDate = proj.endDate
        showDialog = true
    }

    // Add / Edit Project Dialog
    if (showDialog) {
        val totalAmount = NumberFormatUtils.parseAmount(totalAmountInput)
        val calculatedShare = if (myShareType == ProjectEntity.SHARE_TYPE_PERCENTAGE) {
            (totalAmount * 0.15).toLong()
        } else {
            NumberFormatUtils.parseAmount(customAmountInput)
        }

        Dialog(
            onDismissRequest = { showDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth(0.94f)
                    .padding(16.dp)
                    .testTag("project_form_dialog"),
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
                        text = if (editingProject == null) "ثبت پروژه جدید" else "ویرایش پروژه",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Title
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("عنوان پروژه *") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("project_title_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = tokens.glassBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Client Selector
                    if (clients.isNotEmpty()) {
                        ExposedDropdownMenuBox(
                            expanded = clientDropdownExpanded,
                            onExpandedChange = { clientDropdownExpanded = !clientDropdownExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val selectedName = clientMap[selectedClientId]?.name ?: "انتخاب کارفرما"
                            OutlinedTextField(
                                value = selectedName,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("کارفرمای پروژه *") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = clientDropdownExpanded) },
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                                    .testTag("project_client_selector"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = tokens.glassBorder
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = clientDropdownExpanded,
                                onDismissRequest = { clientDropdownExpanded = false }
                            ) {
                                clients.forEach { c ->
                                    DropdownMenuItem(
                                        text = { Text(c.name) },
                                        onClick = {
                                            selectedClientId = c.id
                                            clientDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "ابتدا از برگه کارفرمایان، حداقل یک کارفرما ثبت کنید",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Total Amount
                    LiveThousandSeparatorField(
                        value = totalAmountInput,
                        onValueChange = { totalAmountInput = it },
                        label = "مبلغ کل پروژه *",
                        testTag = "project_total_amount_input"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Financial Calculation Options
                    Text(
                        text = "نحوه محاسبه سهم مالی من:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = myShareType == ProjectEntity.SHARE_TYPE_PERCENTAGE,
                            onClick = { myShareType = ProjectEntity.SHARE_TYPE_PERCENTAGE }
                        )
                        Text(
                            text = "۱۵ درصد پیش‌فرض (سهم من = کل × ۱۵٪)",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.clickable { myShareType = ProjectEntity.SHARE_TYPE_PERCENTAGE }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = myShareType == ProjectEntity.SHARE_TYPE_CUSTOM,
                            onClick = { myShareType = ProjectEntity.SHARE_TYPE_CUSTOM }
                        )
                        Text(
                            text = "مبلغ دلخواه ثابت",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.clickable { myShareType = ProjectEntity.SHARE_TYPE_CUSTOM }
                        )
                    }

                    if (myShareType == ProjectEntity.SHARE_TYPE_CUSTOM) {
                        Spacer(modifier = Modifier.height(8.dp))
                        LiveThousandSeparatorField(
                            value = customAmountInput,
                            onValueChange = { customAmountInput = it },
                            label = "مبلغ دلخواه سهم من *",
                            testTag = "project_custom_share_input"
                        )
                    }

                    // Calculation Summary Banner
                    Spacer(modifier = Modifier.height(10.dp))
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "سهم محاسبه‌شده من:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = tokens.textMuted
                            )
                            Text(
                                text = NumberFormatUtils.formatToman(calculatedShare),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Status
                    Text(
                        text = "وضعیت پروژه:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilterChip(
                            selected = status == ProjectEntity.STATUS_IN_PROGRESS,
                            onClick = { status = ProjectEntity.STATUS_IN_PROGRESS },
                            label = { Text("در حال انجام") },
                            shape = RoundedCornerShape(12.dp)
                        )
                        FilterChip(
                            selected = status == ProjectEntity.STATUS_COMPLETED,
                            onClick = { status = ProjectEntity.STATUS_COMPLETED },
                            label = { Text("تکمیل شده") },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Dates
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = startDate,
                            onValueChange = { startDate = it },
                            label = { Text("تاریخ شروع (شمسی)") },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("project_start_date_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = tokens.glassBorder
                            )
                        )
                        OutlinedTextField(
                            value = endDate,
                            onValueChange = { endDate = it },
                            label = { Text("تاریخ پایان") },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("project_end_date_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = tokens.glassBorder
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Description
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("توضیحات پروژه") },
                        maxLines = 3,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("project_desc_input"),
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
                            modifier = Modifier.testTag("cancel_project_button")
                        ) {
                            Text("انصراف")
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Button(
                            onClick = {
                                val cId = selectedClientId
                                if (title.isNotBlank() && cId != null && totalAmount > 0) {
                                    val customAmt = if (myShareType == ProjectEntity.SHARE_TYPE_CUSTOM) {
                                        NumberFormatUtils.parseAmount(customAmountInput)
                                    } else 0L

                                    val current = editingProject
                                    if (current == null) {
                                        viewModel.addProject(
                                            title = title,
                                            clientId = cId,
                                            totalAmount = totalAmount,
                                            myShareType = myShareType,
                                            customAmount = customAmt,
                                            percentage = 15.0,
                                            description = description,
                                            status = status,
                                            startDate = startDate,
                                            endDate = endDate
                                        )
                                    } else {
                                        viewModel.updateProject(
                                            current.copy(
                                                title = title.trim(),
                                                clientId = cId,
                                                totalAmount = totalAmount,
                                                myShareType = myShareType,
                                                customAmount = customAmt,
                                                description = description.trim(),
                                                status = status,
                                                startDate = startDate,
                                                endDate = endDate
                                            )
                                        )
                                    }
                                    showDialog = false
                                }
                            },
                            enabled = title.isNotBlank() && selectedClientId != null && totalAmount > 0,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("save_project_button")
                        ) {
                            Text(if (editingProject == null) "ثبت پروژه" else "ذخیره تغییرات")
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
                .testTag("projects_list"),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            item {
                Column {
                    Text(
                        text = "مدیریت پروژه‌ها",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "قراردادها، درصد مالی، و پیگیری پیشرفت کار",
                        style = MaterialTheme.typography.bodyMedium,
                        color = tokens.textMuted
                    )
                }
            }

            // Filter Chips
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedStatusFilter == "ALL",
                        onClick = { selectedStatusFilter = "ALL" },
                        label = { Text("همه") },
                        shape = RoundedCornerShape(12.dp)
                    )
                    FilterChip(
                        selected = selectedStatusFilter == "IN_PROGRESS",
                        onClick = { selectedStatusFilter = "IN_PROGRESS" },
                        label = { Text("در حال انجام") },
                        shape = RoundedCornerShape(12.dp)
                    )
                    FilterChip(
                        selected = selectedStatusFilter == "COMPLETED",
                        onClick = { selectedStatusFilter = "COMPLETED" },
                        label = { Text("تکمیل شده") },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            if (filteredProjects.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "هیچ پروژه‌ای یافت نشد",
                            style = MaterialTheme.typography.bodyMedium,
                            color = tokens.textMuted
                        )
                    }
                }
            } else {
                items(filteredProjects, key = { it.id }) { project ->
                    val client = clientMap[project.clientId]

                    SwipeableListItem(
                        isOpen = (openProjectId == project.id),
                        onOpenChange = { isOpen -> openProjectId = if (isOpen) project.id else null },
                        onEdit = {
                            openProjectId = null
                            openEditDialog(project)
                        },
                        onDelete = {
                            openProjectId = null
                            viewModel.deleteProject(project)
                        },
                        deleteConfirmTitle = "حذف پروژه",
                        deleteConfirmMessage = "آیا از حذف پروژه «${project.title}» اطمینان دارید؟ اطلاعات حذف شده قابل بازگشت نیستند."
                    ) {
                        ProjectCardItem(
                            project = project,
                            clientName = client?.name ?: "کارفرمای نامشخص"
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { openAddDialog() },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
                .testTag("add_project_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "ایجاد پروژه جدید")
        }
    }
}

@Composable
private fun ProjectCardItem(
    project: ProjectEntity,
    clientName: String
) {
    val tokens = LocalGlassTokens.current
    val isCompleted = project.status == ProjectEntity.STATUS_COMPLETED

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
                    text = project.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                GlassBadge(
                    text = if (isCompleted) "تکمیل شده" else "در حال انجام",
                    backgroundColor = if (isCompleted) SuccessGreen.copy(alpha = 0.15f) else WarningAmber.copy(alpha = 0.15f),
                    contentColor = if (isCompleted) SuccessGreen else WarningAmber
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "کارفرما",
                    tint = tokens.textMuted,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = clientName,
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.textMuted
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "مبلغ کل پروژه:",
                        style = MaterialTheme.typography.labelSmall,
                        color = tokens.textMuted
                    )
                    Text(
                        text = NumberFormatUtils.formatToman(project.totalAmount),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (project.myShareType == ProjectEntity.SHARE_TYPE_PERCENTAGE) "سهم من (۱۵٪):" else "سهم من (ثابت):",
                        style = MaterialTheme.typography.labelSmall,
                        color = tokens.textMuted
                    )
                    Text(
                        text = NumberFormatUtils.formatToman(project.myShareCalculated),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (project.startDate.isNotEmpty() || project.endDate.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "تاریخ",
                        tint = tokens.textMuted,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = buildString {
                            if (project.startDate.isNotEmpty()) append("شروع: ${project.startDate} ")
                            if (project.endDate.isNotEmpty()) append("• پایان: ${project.endDate}")
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.textMuted
                    )
                }
            }

            if (project.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = project.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.textMuted.copy(alpha = 0.8f),
                    maxLines = 2
                )
            }
        }
    }
}
