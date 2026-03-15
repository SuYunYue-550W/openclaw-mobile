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
import com.openclaw.mobile.api.DomesticModelConfig

/**
 * API 配置界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiConfigScreen(
    onNavigateBack: () -> Unit,
    onSaveApiKey: (String, String) -> Unit
) {
    val scrollState = rememberScrollState()
    
    // API Key 输入状态
    var aliyunKey by remember { mutableStateOf("") }
    var zhipuKey by remember { mutableStateOf("") }
    var baiduKey by remember { mutableStateOf("") }
    var iflytekKey by remember { mutableStateOf("") }
    var lingyiKey by remember { mutableStateOf("") }
    var moonshotKey by remember { mutableStateOf("") }
    
    var showSaveSuccess by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // 顶部栏
        TopAppBar(
            title = { Text("API 配置") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                }
            },
            actions = {
                IconButton(
                    onClick = {
                        onSaveApiKey("aliyun", aliyunKey)
                        onSaveApiKey("zhipu", zhipuKey)
                        onSaveApiKey("baidu", baiduKey)
                        onSaveApiKey("iflytek", iflytekKey)
                        onSaveApiKey("lingyi", lingyiKey)
                        onSaveApiKey("moonshot", moonshotKey)
                        showSaveSuccess = true
                    }
                ) {
                    Icon(Icons.Default.Check, contentDescription = "保存")
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 说明卡片
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
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "使用说明",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• API Key 将使用 AES-256 加密存储在本地\n" +
                                "• 所有数据不会上传到任何服务器\n" +
                                "• 点击顶部 ✓ 按钮保存配置",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // 阿里云 DashScope
            ApiKeyInputCard(
                providerName = "阿里云 DashScope",
                providerId = "aliyun",
                icon = Icons.Default.Cloud,
                value = aliyunKey,
                onValueChange = { aliyunKey = it },
                hint = "请输入阿里云 DashScope API Key",
                docUrl = "https://help.aliyun.com/zh/dashscope/developer-reference/activate-dashscope-and-create-an-api-key"
            )

            // 智谱 AI
            ApiKeyInputCard(
                providerName = "智谱 AI",
                providerId = "zhipu",
                icon = Icons.Default.Api,
                value = zhipuKey,
                onValueChange = { zhipuKey = it },
                hint = "请输入智谱 AI API Key",
                docUrl = "https://open.bigmodel.cn/usercenter/apikeys"
            )

            // 百度文心一言
            ApiKeyInputCard(
                providerName = "百度文心一言",
                providerId = "baidu",
                icon = Icons.Default.Api,
                value = baiduKey,
                onValueChange = { baiduKey = it },
                hint = "请输入百度 API Key",
                docUrl = "https://cloud.baidu.com/doc/WENXINWORKSHOP/index.html"
            )

            // 讯飞星火
            ApiKeyInputCard(
                providerName = "讯飞星火",
                providerId = "iflytek",
                icon = Icons.Default.Api,
                value = iflytekKey,
                onValueChange = { iflytekKey = it },
                hint = "请输入讯飞 API Key",
                docUrl = "https://console.xfyun.cn/services/cbm"
            )

            // 零一万物
            ApiKeyInputCard(
                providerName = "零一万物",
                providerId = "lingyi",
                icon = Icons.Default.Api,
                value = lingyiKey,
                onValueChange = { lingyiKey = it },
                hint = "请输入零一万物 API Key",
                docUrl = "https://platform.lingyiwanwu.com/apikeys"
            )

            // 月之暗面 Kimi
            ApiKeyInputCard(
                providerName = "月之暗面 Kimi",
                providerId = "moonshot",
                icon = Icons.Default.Api,
                value = moonshotKey,
                onValueChange = { moonshotKey = it },
                hint = "请输入月之暗面 API Key",
                docUrl = "https://platform.moonshot.cn/console/api-keys"
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // 保存成功提示
    if (showSaveSuccess) {
        AlertDialog(
            onDismissRequest = { showSaveSuccess = false },
            title = { Text("保存成功") },
            text = { Text("所有 API Key 已加密保存") },
            confirmButton = {
                TextButton(onClick = { showSaveSuccess = false }) {
                    Text("确定")
                }
            }
        )
    }
}

/**
 * API Key 输入卡片
 */
@Composable
fun ApiKeyInputCard(
    providerName: String,
    providerId: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    docUrl: String
) {
    var apiKeyVisible by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    text = providerName,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("API Key") },
                placeholder = { Text(hint) },
                visualTransformation = if (apiKeyVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    IconButton(onClick = { apiKeyVisible = !apiKeyVisible }) {
                        Icon(
                            if (apiKeyVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (apiKeyVisible) "隐藏" else "显示"
                        )
                    }
                },
                shape = MaterialTheme.shapes.small
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            TextButton(
                onClick = {
                    // TODO: 打开浏览器跳转到文档
                },
                modifier = Modifier.align(Alignment.End),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(Icons.Default.OpenInNew, contentDescription = null, Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "获取 API Key",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}
