package com.openclaw.mobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclaw.mobile.api.*
import com.openclaw.mobile.security.SecureStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 聊天 ViewModel
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val modelRepository: ModelRepository,
    private val secureStorage: SecureStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Idle)
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _currentModel = MutableStateFlow<ModelType>(ModelType.Cloud(QwenModel.QWEN_PLUS))
    val currentModel: StateFlow<ModelType> = _currentModel.asStateFlow()

    /**
     * 发送消息
     */
    fun sendMessage(content: String) {
        if (content.isBlank()) return

        viewModelScope.launch {
            val userMessage = Message.user(content)
            _messages.value = _messages.value + userMessage
            _uiState.value = ChatUiState.Loading

            try {
                when (val model = _currentModel.value) {
                    is ModelType.Cloud -> {
                        callCloudApi(model.model, _messages.value)
                    }
                    is ModelType.Local -> {
                        // TODO: 调用本地推理
                        _uiState.value = ChatUiState.Error("本地推理尚未实现")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = ChatUiState.Error(e.message ?: "请求失败")
            }
        }
    }

    /**
     * 调用云端 API
     */
    private suspend fun callCloudApi(model: QwenModel, messages: List<Message>) {
        val result = modelRepository.callQwen(
            messages = messages,
            model = model,
            stream = true
        )

        result.fold(
            onSuccess = { response ->
                val assistantMessage = response.output.choices.firstOrNull()?.message
                    ?: Message.assistant("")
                _messages.value = _messages.value + assistantMessage
                _uiState.value = ChatUiState.Success
            },
            onFailure = { error ->
                _uiState.value = ChatUiState.Error(error.message ?: "请求失败")
            }
        )
    }

    /**
     * 切换模型
     */
    fun switchModel(modelType: ModelType) {
        _currentModel.value = modelType
    }

    /**
     * 清空对话
     */
    fun clearConversation() {
        _messages.value = emptyList()
        _uiState.value = ChatUiState.Idle
    }

    /**
     * 保存 API Key
     */
    fun saveApiKey(provider: String, apiKey: String) {
        viewModelScope.launch {
            modelRepository.saveApiKey(provider, apiKey)
        }
    }
}

/**
 * 模型管理 ViewModel
 */
@HiltViewModel
class ModelViewModel @Inject constructor(
    private val secureStorage: SecureStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow<ModelUiState>(ModelUiState.Loading)
    val uiState: StateFlow<ModelUiState> = _uiState.asStateFlow()

    private val _downloadedModels = MutableStateFlow<Set<String>>(emptySet())
    val downloadedModels: StateFlow<Set<String>> = _downloadedModels.asStateFlow()

    /**
     * 加载已下载的模型列表
     */
    fun loadDownloadedModels() {
        viewModelScope.launch {
            // TODO: 扫描本地模型目录
            _downloadedModels.value = setOf()
            _uiState.value = ModelUiState.Success
        }
    }

    /**
     * 下载模型
     */
    fun downloadModel(modelId: String, downloadUrl: String) {
        viewModelScope.launch {
            _uiState.value = ModelUiState.Downloading(0f)
            // 启动下载服务
            // ModelDownloadService.startDownload(...)
        }
    }

    /**
     * 删除模型
     */
    fun deleteModel(modelId: String) {
        viewModelScope.launch {
            // TODO: 删除本地文件
            _downloadedModels.value = _downloadedModels.value - modelId
        }
    }
}

/**
 * 设置 ViewModel
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val secureStorage: SecureStorage,
    private val modelRepository: ModelRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    /**
     * 加载设置
     */
    fun loadSettings() {
        viewModelScope.launch {
            _uiState.value = SettingsUiState.Success
        }
    }

    /**
     * 保存 API Key
     */
    fun saveApiKey(provider: String, apiKey: String) {
        viewModelScope.launch {
            val result = modelRepository.saveApiKey(provider, apiKey)
            result.fold(
                onSuccess = { _uiState.value = SettingsUiState.Success },
                onFailure = { _uiState.value = SettingsUiState.Error(it.message ?: "保存失败") }
            )
        }
    }

    /**
     * 清除所有数据
     */
    fun clearAllData() {
        viewModelScope.launch {
            secureStorage.clearAll()
            _uiState.value = SettingsUiState.Success
        }
    }
}

/**
 * UI 状态
 */
sealed class ChatUiState {
    object Idle : ChatUiState()
    object Loading : ChatUiState()
    object Success : ChatUiState()
    data class Error(val message: String) : ChatUiState()
}

sealed class ModelUiState {
    object Loading : ModelUiState()
    object Success : ModelUiState()
    data class Downloading(val progress: Float) : ModelUiState()
    data class Error(val message: String) : ModelUiState()
}

sealed class SettingsUiState {
    object Loading : SettingsUiState()
    object Success : SettingsUiState()
    data class Error(val message: String) : SettingsUiState()
}

/**
 * 模型类型
 */
sealed class ModelType {
    data class Cloud(val model: QwenModel) : ModelType()
    data class Local(val modelId: String) : ModelType()
}
