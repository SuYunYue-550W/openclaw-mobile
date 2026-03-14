package com.openclaw.mobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.openclaw.mobile.inference.LocalInferenceEngine
import com.openclaw.mobile.inference.LocalModelConfig

/**
 * 模型管理界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelsScreen() {
    var selectedModelType by remember { mutableStateOf("local") } // "local" or "cloud"
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 模型类型选择
        SegmentedButtonRow(
            selectedModelType = selectedModelType,
            onSelectionChanged = { selectedModelType = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 模型列表
        when (selectedModelType) {
            "local" -> LocalModelsList()
            "cloud" -> CloudModelsList()
        }
    }
}

/**
 * 分段按钮选择器
 */
@Composable
fun SegmentedButtonRow(
    selectedModelType: String,
    onSelectionChanged: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedModelType == "local",
            onClick = { onSelectionChanged("local") },
            label = { Text("本地模型") },
            leadingIcon = if (selectedModelType == "local") {
                { Icon(Icons.Default.Download, contentDescription = null, Modifier.size(18.dp)) }
            } else null,
            modifier = Modifier.weight(1f)
        )

        FilterChip(
            selected = selectedModelType == "cloud",
            onClick = { onSelectionChanged("cloud") },
            label = { Text("云端 API") },
            leadingIcon = if (selectedModelType == "cloud") {
                { Icon(Icons.Default.Cloud, contentDescription = null, Modifier.size(18.dp)) }
            } else null,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * 本地模型列表
 */
@Composable
fun LocalModelsList() {
    val models = LocalInferenceEngine.SUPPORTED_MODELS
    
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(models) { model ->
            LocalModelCard(model = model)
        }
    }
}

/**
 * 本地模型卡片
 */
@Composable
fun LocalModelCard(model: LocalModelConfig) {
    var isDownloading by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableStateOf(0f) }
    
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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = model.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (model.isRecommended) {
                            AssistChip(
                                onClick = { },
                                label = { Text("推荐") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Star,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = model.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "大小：${model.sizeGB}GB",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            text = "内存：${model.ramRequired}GB RAM",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                if (isDownloading) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            progress = downloadProgress,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = "${(downloadProgress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                } else {
                    FilledTonalButton(
                        onClick = {
                            // TODO: 开始下载
                            isDownloading = true
                        },
                        enabled = !isDownloading
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("下载")
                    }
                }
            }

            // 下载链接
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedButton(
                onClick = {
                    // TODO: 打开浏览器跳转到下载页面
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(Icons.Default.OpenInNew, contentDescription = null, Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("前往下载源")
            }
        }
    }
}

/**
 * 云端模型列表
 */
@Composable
fun CloudModelsList() {
    // TODO: 显示云端 API 配置界面
    Text("云端 API 配置界面开发中...")
}
