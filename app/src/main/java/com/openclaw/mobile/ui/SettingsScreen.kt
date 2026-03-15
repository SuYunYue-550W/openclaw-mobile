package com.openclaw.mobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 设置界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToApiConfig: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "设置",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // API 配置区域
        SettingsSection(title = "API 配置", icon = Icons.Default.Cloud) {
            SettingsItem(
                title = "API Key 管理",
                subtitle = "配置各大模型厂商的 API Key",
                icon = Icons.Default.Cloud,
                trailing = {
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                onClick = onNavigateToApiConfig
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 安全与隐私
        SettingsSection(title = "安全与隐私", icon = Icons.Default.Security) {
            SettingsItem(
                title = "数据加密",
                subtitle = "所有本地数据已使用 AES-256 加密",
                icon = Icons.Default.Lock,
                trailing = {
                    AssistChip(
                        onClick = { },
                        label = { Text("已启用") }
                    )
                }
            )

            SettingsItem(
                title = "权限管理",
                subtitle = "管理应用权限",
                icon = Icons.Default.Shield
            )

            SettingsItem(
                title = "清除所有数据",
                subtitle = "删除所有本地存储的对话和设置",
                icon = Icons.Default.DeleteForever,
                onClick = {
                    // TODO: 显示确认对话框
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 模型设置
        SettingsSection(title = "模型设置", icon = Icons.Default.Psychology) {
            SettingsItem(
                title = "默认模型",
                subtitle = "选择默认使用的模型",
                icon = Icons.Default.Star
            )

            SettingsItem(
                title = "本地模型路径",
                subtitle = "/storage/emulated/0/Android/data/com.openclaw.mobile/files/models",
                icon = Icons.Default.Folder
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 关于
        SettingsSection(title = "关于", icon = Icons.Default.Info) {
            SettingsItem(
                title = "应用版本",
                subtitle = "1.0.0 (2026.03.14)",
                icon = Icons.Default.Info
            )

            SettingsItem(
                title = "开源协议",
                subtitle = "MIT License",
                icon = Icons.Default.Description
            )

            SettingsItem(
                title = "查看源代码",
                subtitle = "GitHub: openclaw/openclaw",
                icon = Icons.Default.Code,
                onClick = {
                    // TODO: 打开 GitHub
                }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

/**
 * 设置区域
 */
@Composable
fun SettingsSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            content()
        }
    }
}

/**
 * 设置项
 */
@Composable
fun SettingsItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    trailing: @Composable () -> Unit = {},
    onClick: () -> Unit = {}
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle) },
        leadingContent = {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingContent = trailing,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}
