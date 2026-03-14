package com.openclaw.mobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.openclaw.mobile.api.DomesticModelConfig
import com.openclaw.mobile.api.QwenModel

/**
 * API Key 配置界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiConfigScreen(
    onNavigateBack: () -> Unit,
    onSaveApiKey: (String, String) -> Unit
) {
    var selectedProvider by remember { mutableStateOf<ApiProvider?>(null) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 顶部栏
        TopAppBar(
            title = { Text("API 配置") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedProvider == null) {
            // 提供商列表
            ProviderList(onProviderSelected = { selectedProvider = it })
        } else {
            // API Key 输入界面
            ApiKeyInputForm(
                provider = selectedProvider!!,
                onSave = { provider, key ->
                    onSaveApiKey(provider, key)
                    selectedProvider = null
                },
                onBack = { selectedProvider = null }
            )
        }
    }
}

/**
 * 提供商列表
 */
@Composable
fun ProviderList(onProviderSelected: (ApiProvider) -> Unit) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "选择 API 提供商",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 阿里云
        ProviderCard(
            name = "阿里云 DashScope",
            models = "通义千问 Turbo/Plus/Max",
            icon = Icons.Default.Cloud,
            isRecommended = true,
            onClick = { onProviderSelected(ApiProvider.Aliyun) }
        )

        // 智谱 AI
        ProviderCard(
            name = "智谱 AI",
            models = "ChatGLM Pro/Max",
            icon = Icons.Default.Psychology,
            isRecommended = false,
            onClick = { onProviderSelected(ApiProvider.Zhipu) }
        )

        // 百度文心一言
        ProviderCard(
            name = "百度 文心一言",
            models = "ERNIE Bot 4.0",
            icon = Icons.Default.AutoAwesome,
            isRecommended = false,
            onClick = { onProviderSelected(ApiProvider.Baidu) }
        )

        // 讯飞星火
        ProviderCard(
            name = "讯飞 星火认知",
            models = "Spark V3.5",
            icon = Icons.Default.Lightbulb,
            isRecommended = false,
            onClick = { onProviderSelected(ApiProvider.Iflytek) }
        )

        // 零一万物
        ProviderCard(
            name = "零一万物",
            models = "Yi-34B",
            icon = Icons.Default.Star,
            isRecommended = false,
            onClick = { onProviderSelected(ApiProvider.Lingyi) }
        )

        // 月之暗面
        ProviderCard(
            name = "月之暗面 Kimi",
            models = "Moonshot V1",
            icon = Icons.Default.NightsStay,
            isRecommended = false,
            onClick = { onProviderSelected(ApiProvider.Moonshot) }
        )

        // 帮助信息
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.infoContainer
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
                        tint = MaterialTheme.colorScheme.onInfoContainer
                    )
                    Text(
                        text = "如何获取 API Key",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onInfoContainer
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "1. 访问对应平台的官方网站\n" +
                           "2. 注册/登录账号\n" +
                           "3. 进入控制台创建 API Key\n" +
                           "4. 复制并粘贴到此处",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onInfoContainer
                )
            }
        }
    }
}

/**
 * 提供商卡片
 */
@Composable
fun ProviderCard(
    name: String,
    models: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isRecommended: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (isRecommended) {
                        AssistChip(
                            onClick = { },
                            label = { Text("推荐") },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
                Text(
                    text = models,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "配置",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * API Key 输入表单
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiKeyInputForm(
    provider: ApiProvider,
    onSave: (String, String) -> Unit,
    onBack: () -> Unit
) {
    var apiKey by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var isValid by remember { mutableStateOf(true) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 标题
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "返回")
            }
            
            Text(
                text = provider.name,
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 说明
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "请输入${provider.name}的 API Key",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "API Key 将使用 AES-256 加密存储在本地，不会上传到任何服务器。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 输入框
        OutlinedTextField(
            value = apiKey,
            onValueChange = { 
                apiKey = it
                isValid = it.isNotBlank()
            },
            label = { Text("API Key") },
            placeholder = { Text("请输入 API Key") },
            visualTransformation = if (showPassword) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            singleLine = false,
            minLines = 3,
            maxLines = 5,
            isError = !isValid,
            supportingText = if (!isValid) {
                { Text("API Key 不能为空") }
            } else null,
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (showPassword) "隐藏" else "显示"
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 保存按钮
        Button(
            onClick = {
                if (apiKey.isNotBlank()) {
                    onSave(provider.key, apiKey.trim())
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = apiKey.isNotBlank()
        ) {
            Icon(Icons.Default.Save, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("保存 API Key")
        }

        // 获取 API Key 链接
        TextButton(
            onClick = {
                // TODO: 打开浏览器跳转到官方文档
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("如何获取 API Key？")
        }
    }
}

/**
 * API 提供商枚举
 */
enum class ApiProvider(
    val key: String,
    val name: String,
    val website: String
) {
    Aliyun("aliyun", "阿里云 DashScope", "https://dashscope.console.aliyun.com/"),
    Zhipu("zhipu", "智谱 AI", "https://open.bigmodel.cn/"),
    Baidu("baidu", "百度 文心一言", "https://cloud.baidu.com/product/wenxinworkshop"),
    Iflytek("iflytek", "讯飞 星火", "https://www.xfyun.cn/service/spark"),
    Lingyi("lingyi", "零一万物", "https://platform.lingyiwanwu.com/"),
    Moonshot("moonshot", "月之暗面", "https://platform.moonshot.cn/")
}

// 扩展颜色方案
val MaterialTheme.colorScheme.infoContainer
    get() = primaryContainer.copy(alpha = 0.5f)

val MaterialTheme.colorScheme.onInfoContainer
    get() = onPrimaryContainer
