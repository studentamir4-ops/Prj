package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PriceCheck
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalGlassTokens
import com.example.viewmodel.MainViewModel

enum class AppScreen(val title: String, val icon: ImageVector) {
    DASHBOARD("داشبورد", Icons.Default.Dashboard),
    PROJECTS("پروژه‌ها", Icons.Default.Work),
    CLIENTS("کارفرمایان", Icons.Default.People),
    RECEIPTS("دریافتی‌ها", Icons.Default.PriceCheck),
    WORKDAYS("روزهای کاری", Icons.Default.CalendarMonth),
    SETTINGS("تنظیمات", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    var currentScreen by remember { mutableStateOf(AppScreen.DASHBOARD) }
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()
    val tokens = LocalGlassTokens.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle back button: if not on DASHBOARD, go back to DASHBOARD
    BackHandler(enabled = currentScreen != AppScreen.DASHBOARD) {
        currentScreen = AppScreen.DASHBOARD
    }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissUserMessage()
        }
    }

    // Wrap everything in RTL layout direction for true Persian interface
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(tokens.backgroundGradient)
        ) {
            Scaffold(
                containerColor = Color.Transparent,
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = "مدیریت پروژه و مالی",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        actions = {
                            // Quick Theme Toggle
                            IconButton(
                                onClick = { viewModel.toggleDarkMode() },
                                modifier = Modifier.testTag("top_bar_theme_toggle")
                            ) {
                                Icon(
                                    imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                    contentDescription = "تغییر تم",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            // Settings Action Button
                            IconButton(
                                onClick = {
                                    currentScreen = if (currentScreen == AppScreen.SETTINGS) AppScreen.DASHBOARD else AppScreen.SETTINGS
                                },
                                modifier = Modifier.testTag("top_bar_settings")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "تنظیمات",
                                    tint = if (currentScreen == AppScreen.SETTINGS) MaterialTheme.colorScheme.primary else tokens.textMuted
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent
                        ),
                        modifier = Modifier.statusBarsPadding()
                    )
                },
                bottomBar = {
                    GlassBottomNavigationBar(
                        currentScreen = currentScreen,
                        onScreenSelected = { currentScreen = it }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            fadeIn().togetherWith(fadeOut())
                        },
                        label = "screen_transition"
                    ) { screen ->
                        when (screen) {
                            AppScreen.DASHBOARD -> DashboardScreen(
                                viewModel = viewModel,
                                onNavigateToProjects = { currentScreen = AppScreen.PROJECTS },
                                onNavigateToReceipts = { currentScreen = AppScreen.RECEIPTS }
                            )
                            AppScreen.PROJECTS -> ProjectsScreen(viewModel = viewModel)
                            AppScreen.CLIENTS -> ClientsScreen(viewModel = viewModel)
                            AppScreen.RECEIPTS -> ReceiptsScreen(viewModel = viewModel)
                            AppScreen.WORKDAYS -> WorkdaysScreen(viewModel = viewModel)
                            AppScreen.SETTINGS -> SettingsScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GlassBottomNavigationBar(
    currentScreen: AppScreen,
    onScreenSelected: (AppScreen) -> Unit
) {
    val tokens = LocalGlassTokens.current
    val navScreens = listOf(
        AppScreen.DASHBOARD,
        AppScreen.PROJECTS,
        AppScreen.CLIENTS,
        AppScreen.RECEIPTS,
        AppScreen.WORKDAYS
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(12.dp, RoundedCornerShape(26.dp))
                .clip(RoundedCornerShape(26.dp))
                .background(tokens.cardGradient)
                .border(1.dp, tokens.glassBorder, RoundedCornerShape(26.dp))
        ) {
            navScreens.forEach { screen ->
                val selected = (currentScreen == screen)
                NavigationBarItem(
                    selected = selected,
                    onClick = { onScreenSelected(screen) },
                    icon = {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = screen.title,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = screen.title,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = tokens.textMuted,
                        unselectedTextColor = tokens.textMuted,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_tab_${screen.name}")
                )
            }
        }
    }
}
