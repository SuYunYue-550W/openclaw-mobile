package com.openclaw.mobile.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.openclaw.mobile.security.PermissionManager

/**
 * 权限请求界面
 */
@Composable
fun PermissionRequestScreen(
    onPermissionsGranted: () -> Unit,
    onPermissionsDenied: () -> Unit
) {
    val context = LocalContext.current
    val permissionManager = remember { PermissionManager() }
    
    var requiredPermissions by remember {
        mutableStateOf(
            listOf(
                PermissionInfo(
                    permission = Manifest.permission.INTERNET,
                    title = "网络访问",
                    description = "用于连接云端 API 和下载模型文件",
                    icon = Icons.Default.Cloud,
                    isCritical = true
                ),
                PermissionInfo(
                    permission = Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    title = "存储访问",
                    description = "用于保存本地模型文件和对话历史",
                    icon = Icons.Default.Folder,
                    isCritical = true
                ),
                PermissionInfo(
                    permission = Manifest.permission.POST_NOTIFICATIONS,
                    title = "通知",
                    description = "用于显示下载进度和消息提醒",
                    icon = Icons.Default.Notifications,
                    isCritical = false
                )
            )
        )
    }
    
    val multiplePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            onPermissionsGranted()
        } else {
            // 部分权限被拒绝，显示说明
        }
    }
    
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scrollState)
    ) {
        // 标题
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Default.Shield,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Column {
                Text(
                    text = "权限说明",
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(
                    text = "为了保护您的隐私，我们需要以下权限",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // 权限列表
        permissionPermissions.forEach { permission ->
            PermissionItem(permission = permission)
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 安全承诺
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "安全承诺",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "• 所有权限仅用于应用核心功能\n" +
                           "• 不会收集或上传您的个人信息\n" +
                           "• 数据本地加密存储\n" +
                           "• 可随时在系统设置中撤销权限",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // 按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onPermissionsDenied,
                modifier = Modifier.weight(1f)
            ) {
                Text("拒绝")
            }
            
            Button(
                onClick = {
                    val permissions = permissionPermissions.map { it.permission }.toTypedArray()
                    multiplePermissionLauncher.launch(permissions)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("同意并继续")
            }
        }
    }
}

/**
 * 权限项
 */
@Composable
fun PermissionItem(permission: PermissionInfo) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                permission.icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = if (permission.isCritical) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = permission.title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (permission.isCritical) {
                        AssistChip(
                            onClick = { },
                            label = { Text("必需") },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = permission.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * 权限信息
 */
data class PermissionInfo(
    val permission: String,
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val isCritical: Boolean
)
