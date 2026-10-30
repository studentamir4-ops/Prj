package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.components.GlassCard
import com.example.ui.theme.LocalGlassTokens
import com.example.ui.theme.SuccessGreen
import com.example.viewmodel.MainViewModel

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val tokens = LocalGlassTokens.current
    val context = LocalContext.current

    var showBackupDialog by remember { mutableStateOf(false) }
    var backupJsonContent by remember { mutableStateOf("") }
    var showRestoreDialog by remember { mutableStateOf(false) }
    var restoreInputJson by remember { mutableStateOf("") }

    // Backup Output Dialog
    if (showBackupDialog) {
        Dialog(
            onDismissRequest = { showBackupDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .padding(16.dp)
                    .testTag("backup_export_dialog"),
                shape = RoundedCornerShape(24.dp),
                elevation = 16.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "نسخه پشتیبان از کل اطلاعات",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "متن زیر شامل کلیه پروژه‌ها، کارفرمایان، دریافتی‌ها و روزهای کاری شماست. می‌توانید آن را کپی و ذخیره کنید:",
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.textMuted
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 240.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(tokens.glassSurfaceVariant.copy(alpha = 0.5f))
                            .padding(12.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = backupJsonContent,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("ProjectFinanceBackup", backupJsonContent)
                                clipboard.setPrimaryClip(clip)
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("copy_backup_button")
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("کپی در کلیپ‌بورد")
                        }

                        OutlinedButton(
                            onClick = { showBackupDialog = false },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("بستن")
                        }
                    }
                }
            }
        }
    }

    // Restore Input Dialog
    if (showRestoreDialog) {
        Dialog(
            onDismissRequest = { showRestoreDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .padding(16.dp)
                    .testTag("restore_import_dialog"),
                shape = RoundedCornerShape(24.dp),
                elevation = 16.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "بازیابی اطلاعات از فایل پشتیبان",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "کد JSON نسخه پشتیبان را در کادر زیر وارد کنید. اطلاعات موجود حفظ شده و اطلاعات پشتیبان به پایگاه داده اضافه خواهند شد:",
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.textMuted
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = restoreInputJson,
                        onValueChange = { restoreInputJson = it },
                        placeholder = { Text("متن JSON پشتیبان را اینجا جای‌گذاری (Paste) کنید...") },
                        maxLines = 8,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .testTag("restore_json_input"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = tokens.glassBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = { showRestoreDialog = false },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("انصراف")
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Button(
                            onClick = {
                                if (restoreInputJson.isNotBlank()) {
                                    viewModel.restoreBackup(restoreInputJson)
                                    showRestoreDialog = false
                                }
                            },
                            enabled = restoreInputJson.isNotBlank(),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("execute_restore_button")
                        ) {
                            Text("شروع بازیابی")
                        }
                    }
                }
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen"),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Header
        item {
            Column {
                Text(
                    text = "تنظیمات برنامه",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "شخصی‌سازی ظاهر و مدیریت پشتیبان‌گیری داده‌ها",
                    style = MaterialTheme.typography.bodyMedium,
                    color = tokens.textMuted
                )
            }
        }

        // Section 1: Appearance & Theme
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "تنظیمات ظاهری و پوسته (Theme)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = null,
                                tint = if (isDarkMode) MaterialTheme.colorScheme.primary else Color(0xFFF59E0B),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isDarkMode) "تم تاریک (Dark Mode)" else "تم روشن (Light Mode)",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "طراحی شیشه‌ای با کنتراست دقیق و شفافیت کنترل‌شده",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = tokens.textMuted
                                )
                            }
                        }

                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { viewModel.toggleDarkMode() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.testTag("theme_switch")
                        )
                    }
                }
            }
        }

        // Section 2: Backup & Restore
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Backup,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "پشتیبان‌گیری و بازیابی داده‌ها (Backup & Restore)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "اطلاعات شما به‌صورت کاملاً دائمی و ایمن در حافظه داخلی ذخیره می‌شود. جهت اطمینان بیشتر، می‌توانید نسخه پشتیبان تهیه کنید.",
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.textMuted
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.generateBackup { json ->
                                    backupJsonContent = json
                                    showBackupDialog = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("backup_button")
                        ) {
                            Icon(Icons.Default.Backup, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("تهیه پشتیبان")
                        }

                        OutlinedButton(
                            onClick = {
                                restoreInputJson = ""
                                showRestoreDialog = true
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("restore_button")
                        ) {
                            Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("بازیابی اطلاعات")
                        }
                    }
                }
            }
        }

        // Section 3: App Information & Typography
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = 3.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = tokens.textMuted,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "مشخصات فنی و تایپوگرافی",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "• فونت اختصاصی: وزیرمتن (Vazirmatn) برای تمام اوزان",
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.textMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• پایگاه داده: Room با ذخیره‌سازی محلی و ایزوله",
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.textMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• طراحی رابط کاربری: Jetpack Compose و Material 3 با متدولوژی Glassmorphism",
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.textMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• نسخه برنامه: ۱.۰.۰",
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.textMuted
                    )
                }
            }
        }
    }
}
