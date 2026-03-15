package com.openclaw.mobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.openclaw.mobile.viewmodel.ChatViewModel

/**
 * 应用主界面
 */
@Composable
fun OpenClawApp() {
    val navController = rememberNavController()
    var selectedTab by remember { mutableStateOf(0) }
    val viewModel: ChatViewModel = hiltViewModel()

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Chat, contentDescription = "聊天") },
                    label = { Text("聊天") },
                    selected = selectedTab == 0,
                    onClick = {
                        selectedTab = 0
                        navController.navigate("chat") {
                            popUpTo("chat") { inclusive = true }
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.CloudDownload, contentDescription = "模型") },
                    label = { Text("模型") },
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        navController.navigate("models") {
                            popUpTo("models") { inclusive = true }
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "设置") },
                    label = { Text("设置") },
                    selected = selectedTab == 2,
                    onClick = {
                        selectedTab = 2
                        navController.navigate("settings") {
                            popUpTo("settings") { inclusive = true }
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "chat",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("chat") { ChatScreen() }
            composable("models") { ModelsScreen() }
            composable("settings") { 
                SettingsScreen(
                    onNavigateToApiConfig = {
                        navController.navigate("api-config")
                    }
                ) 
            }
            composable("api-config") {
                ApiConfigScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onSaveApiKey = { provider, key ->
                        viewModel.saveApiKey(provider, key)
                    }
                )
            }
        }
    }
}
