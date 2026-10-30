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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.data.model.ClientEntity
import com.example.ui.components.GlassCard
import com.example.ui.components.SwipeableListItem
import com.example.ui.theme.LocalGlassTokens
import com.example.viewmodel.MainViewModel

@Composable
fun ClientsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val clients by viewModel.clients.collectAsState()
    val tokens = LocalGlassTokens.current
    var searchQuery by remember { mutableStateOf("") }

    // Dialog state for add/edit client
    var showDialog by remember { mutableStateOf(false) }
    var editingClient by remember { mutableStateOf<ClientEntity?>(null) }
    var clientName by remember { mutableStateOf("") }
    var clientPhone by remember { mutableStateOf("") }
    var clientAddress by remember { mutableStateOf("") }

    val filteredClients = remember(clients, searchQuery) {
        if (searchQuery.isBlank()) {
            clients
        } else {
            val q = searchQuery.trim().lowercase()
            clients.filter {
                it.name.lowercase().contains(q) ||
                        it.address.lowercase().contains(q) ||
                        it.phone.contains(q)
            }
        }
    }

    fun openAddDialog() {
        editingClient = null
        clientName = ""
        clientPhone = ""
        clientAddress = ""
        showDialog = true
    }

    fun openEditDialog(client: ClientEntity) {
        editingClient = client
        clientName = client.name
        clientPhone = client.phone
        clientAddress = client.address
        showDialog = true
    }

    // Add / Edit Client Dialog
    if (showDialog) {
        Dialog(
            onDismissRequest = { showDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .padding(16.dp)
                    .testTag("client_form_dialog"),
                shape = RoundedCornerShape(24.dp),
                elevation = 16.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = if (editingClient == null) "ثبت کارفرمای جدید" else "ویرایش اطلاعات کارفرما",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = clientName,
                        onValueChange = { clientName = it },
                        label = { Text("نام کارفرما *") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("client_name_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = tokens.glassBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = clientPhone,
                        onValueChange = { clientPhone = it },
                        label = { Text("شماره تماس") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("client_phone_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = tokens.glassBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = clientAddress,
                        onValueChange = { clientAddress = it },
                        label = { Text("آدرس کارفرما") },
                        maxLines = 3,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("client_address_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = tokens.glassBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = { showDialog = false },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("cancel_client_button")
                        ) {
                            Text("انصراف")
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Button(
                            onClick = {
                                if (clientName.isNotBlank()) {
                                    val current = editingClient
                                    if (current == null) {
                                        viewModel.addClient(clientName, clientPhone, clientAddress)
                                    } else {
                                        viewModel.updateClient(
                                            current.copy(
                                                name = clientName.trim(),
                                                phone = clientPhone.trim(),
                                                address = clientAddress.trim()
                                            )
                                        )
                                    }
                                    showDialog = false
                                }
                            },
                            enabled = clientName.isNotBlank(),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("save_client_button")
                        ) {
                            Text(if (editingClient == null) "ثبت کارفرما" else "ذخیره تغییرات")
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
                .testTag("clients_list"),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Screen Header
            item {
                Column {
                    Text(
                        text = "مدیریت کارفرمایان",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "فهرست اشخاص و شرکت‌های طرف قرارداد",
                        style = MaterialTheme.typography.bodyMedium,
                        color = tokens.textMuted
                    )
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("جستجوی نام یا آدرس کارفرما...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "جستجو",
                            tint = tokens.textMuted
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "پاک کردن",
                                    tint = tokens.textMuted
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = tokens.glassBorder,
                        focusedContainerColor = tokens.glassSurfaceVariant.copy(alpha = 0.4f),
                        unfocusedContainerColor = tokens.glassSurfaceVariant.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("client_search_input")
                )
            }

            if (filteredClients.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchQuery.isEmpty()) "هیچ کارفرمایی ثبت نشده است" else "کارفرمایی با این مشخصات یافت نشد",
                            style = MaterialTheme.typography.bodyMedium,
                            color = tokens.textMuted
                        )
                    }
                }
            } else {
                items(filteredClients, key = { it.id }) { client ->
                    SwipeableListItem(
                        onEdit = { openEditDialog(client) },
                        onDelete = { viewModel.deleteClient(client) },
                        deleteConfirmTitle = "حذف کارفرما",
                        deleteConfirmMessage = "آیا از حذف «${client.name}» اطمینان دارید؟ پروژه‌ها و دریافتی‌های متصل به این کارفرما حفظ خواهند شد."
                    ) {
                        ClientCardItem(client = client)
                    }
                }
            }
        }

        // Floating Action Button to Add Client
        FloatingActionButton(
            onClick = { openAddDialog() },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
                .testTag("add_client_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "ثبت کارفرمای جدید")
        }
    }
}

@Composable
private fun ClientCardItem(client: ClientEntity) {
    val tokens = LocalGlassTokens.current

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = client.name,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = client.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (client.phone.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "تلفن",
                            tint = tokens.textMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = client.phone,
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.textMuted
                        )
                    }
                }

                if (client.address.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "آدرس",
                            tint = tokens.textMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = client.address,
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.textMuted,
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}
