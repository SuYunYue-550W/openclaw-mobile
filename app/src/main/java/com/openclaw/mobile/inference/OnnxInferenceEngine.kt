package com.openclaw.mobile.inference

import android.content.Context
import android.util.Log
import ai.onnxruntime.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.FloatBuffer
import java.nio.LongBuffer

/**
 * ONNX 推理引擎实现
 * 
 * 基于 ONNX Runtime 进行本地大模型推理
 * 支持 Qwen、ChatGLM 等模型的 ONNX 格式
 */
class OnnxInferenceEngine(
    private val context: Context
) {
    companion object {
        private const val TAG = "OnnxInference"
        private const val MAX_SEQUENCE_LENGTH = 2048
        private const val HIDDEN_SIZE = 1536 // Qwen2.5-1.5B
    }

    private var ortEnvironment: OrtEnvironment? = null
    private var ortSession: OrtSession? = null
    private var tokenizer: SimpleTokenizer? = null
    private var isInitialized = false
    private var currentModelConfig: LocalModelConfig? = null

    /**
     * 初始化 ONNX 推理引擎
     */
    suspend fun initialize(modelPath: String, modelConfig: LocalModelConfig): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Log.i(TAG, "开始初始化 ONNX 引擎：$modelPath")
            
            // 创建 ONNX 环境
            ortEnvironment = OrtEnvironment.getEnvironment()
            
            // 创建会话选项
            val sessionOptions = OrtSession.SessionOptions().apply {
                // 设置线程数
                setIntraOpNumThreads(4)
                setInterOpNumThreads(2)
                
                // 优化级别
                setSessionOptimizationLevel(OrtSession.SessionOptions.OptLevel.ALL_OPT)
                
                // 添加 CPU 执行提供者
                addCPU(OrtSession.SessionOptions.CPUOptions().apply {
                    setArenaExtendStrategy(1) // 扩展内存池
                })
                
                // 如果有 GPU，添加 GPU 执行提供者
                try {
                    // addCUDA(0) // 需要 GPU 支持
                } catch (e: Exception) {
                    Log.w(TAG, "GPU 不可用，使用 CPU 推理")
                }
            }
            
            // 加载模型
            val modelFile = File(modelPath)
            if (!modelFile.exists()) {
                return@withContext Result.failure(Exception("模型文件不存在：$modelPath"))
            }
            
            ortSession = ortEnvironment?.createSession(modelFile.absolutePath, sessionOptions)
            
            // 初始化分词器
            tokenizer = SimpleTokenizer(context)
            
            currentModelConfig = modelConfig
            isInitialized = true
            
            Log.i(TAG, "ONNX 引擎初始化成功")
            Log.i(TAG, "模型输入节点：${ortSession?.inputNames}")
            Log.i(TAG, "模型输出节点：${ortSession?.outputNames}")
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "ONNX 引擎初始化失败", e)
            Result.failure(e)
        }
    }

    /**
     * 生成回复（流式）
     */
    fun generate(
        prompt: String,
        systemPrompt: String = "你是一个有帮助的助手。",
        maxTokens: Int = 512,
        temperature: Float = 0.7f
    ): Flow<String> = flow {
        if (!isInitialized) {
            emit("[错误] 模型未初始化")
            return@flow
        }

        try {
            // 1. 分词
            val inputIds = tokenizer?.encode(prompt, systemPrompt)
                ?: return@flow
            
            // 2. 准备输入张量
            val inputShape = longArrayOf(1, inputIds.size.toLong())
            val inputBuffer = LongBuffer.wrap(inputIds.toLongArray())
            val inputTensor = OnnxTensor.createTensor(
                ortEnvironment,
                inputBuffer,
                inputShape
            )
            
            // 3. 创建输入映射
            val inputs = mutableMapOf<String, OnnxTensor>()
            ortSession?.inputNames?.forEachIndexed { index, name ->
                inputs[name] = if (index == 0) inputTensor else createAttentionMask(inputIds)
            }
            
            // 4. 运行推理
            val results = ortSession?.run(inputs)
            
            // 5. 处理输出
            results?.use { output ->
                val outputTensor = output.get(0) as OnnxTensor
                val outputData = outputTensor.value as Array<FloatArray>
                
                // 6. 解码输出
                val tokens = decodeOutput(outputData, temperature)
                tokens.forEach { token ->
                    emit(token)
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "推理失败", e)
            emit("[错误] ${e.message}")
        }
    }.flowOn(Dispatchers.IO)

    /**
     * 创建注意力掩码
     */
    private fun createAttentionMask(inputIds: List<Int>): OnnxTensor {
        val mask = inputIds.map { if (it == 0) 0L else 1L }.toLongArray()
        val maskBuffer = LongBuffer.wrap(mask)
        val maskShape = longArrayOf(1, mask.size.toLong())
        return OnnxTensor.createTensor(ortEnvironment, maskBuffer, maskShape)
    }

    /**
     * 解码输出
     */
    private fun decodeOutput(outputData: Array<FloatArray>, temperature: Float): List<String> {
        val tokens = mutableListOf<String>()
        
        outputData.forEach { logits ->
            // 应用 temperature
            val scaledLogits = logits.map { it / temperature }
            
            // Softmax
            val probabilities = softmax(scaledLogits)
            
            // 采样
            val tokenId = sampleFromProbabilities(probabilities)
            
            // 解码 token
            val token = tokenizer?.decode(tokenId) ?: ""
            if (token.isNotEmpty() && token != "</s>") {
                tokens.add(token)
            }
        }
        
        return tokens
    }

    /**
     * Softmax 函数
     */
    private fun softmax(logits: List<Float>): List<Float> {
        val maxLogit = logits.maxOrNull() ?: 0f
        val exps = logits.map { Math.exp((it - maxLogit).toDouble()).toFloat() }
        val sumExps = exps.sum()
        return exps.map { it / sumExps }
    }

    /**
     * 从概率分布采样
     */
    private fun sampleFromProbabilities(probabilities: List<Float>): Int {
        val random = Math.random().toFloat()
        var cumSum = 0f
        probabilities.forEachIndexed { index, prob ->
            cumSum += prob
            if (random <= cumSum) return index
        }
        return probabilities.size - 1
    }

    /**
     * 批量推理（用于对话历史）
     */
    suspend fun batchGenerate(
        messages: List<Map<String, String>>,
        maxTokens: Int = 512
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (!isInitialized) {
                return@withContext Result.failure(Exception("模型未初始化"))
            }
            
            // 构建对话文本
            val prompt = buildPrompt(messages)
            
            // 生成回复
            val response = StringBuilder()
            generate(prompt, maxTokens = maxTokens).collect { token ->
                response.append(token)
            }
            
            Result.success(response.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 构建对话提示词
     */
    private fun buildPrompt(messages: List<Map<String, String>>): String {
        val sb = StringBuilder()
        messages.forEach { message ->
            val role = message["role"] ?: "user"
            val content = message["content"] ?: ""
            when (role) {
                "system" -> sb.append("[SYSTEM] $content\n")
                "user" -> sb.append("[USER] $content\n")
                "assistant" -> sb.append("[ASSISTANT] $content\n")
            }
        }
        sb.append("[ASSISTANT] ")
        return sb.toString()
    }

    /**
     * 释放资源
     */
    fun release() {
        try {
            ortSession?.close()
            ortEnvironment?.close()
            tokenizer?.release()
            isInitialized = false
            Log.i(TAG, "资源已释放")
        } catch (e: Exception) {
            Log.e(TAG, "释放资源失败", e)
        }
    }

    /**
     * 检查是否已初始化
     */
    fun isInitialized(): Boolean = isInitialized

    /**
     * 获取当前模型配置
     */
    fun getCurrentModelConfig(): LocalModelConfig? = currentModelConfig
}

/**
 * 简单分词器（示例实现）
 * 实际项目中应使用 HuggingFace Tokenizer 或类似库
 */
class SimpleTokenizer(private val context: Context) {
    private val vocab: Map<String, Int> by lazy {
        // 从 assets 加载词表
        loadVocabFromAssets()
    }
    
    private val idToToken: Map<Int, String> by lazy {
        vocab.entries.associateBy({ it.value }, { it.key })
    }

    /**
     * 编码文本
     */
    fun encode(text: String, systemPrompt: String = ""): List<Int> {
        val fullText = if (systemPrompt.isNotEmpty()) {
            "$systemPrompt\n$text"
        } else {
            text
        }
        
        // 简单的字符级分词（示例）
        // 实际应使用 BPE 或 WordPiece
        return fullText.toCharArray().map { char ->
            vocab[char.toString()] ?: vocab["[UNK]"] ?: 0
        }
    }

    /**
     * 解码 token
     */
    fun decode(tokenId: Int): String {
        return idToToken[tokenId] ?: ""
    }

    /**
     * 加载词表
     */
    private fun loadVocabFromAssets(): Map<String, Int> {
        return try {
            val inputStream = context.assets.open("vocab.txt")
            val content = inputStream.bufferedReader().use { it.readText() }
            content.lines().mapIndexed { index, line ->
                line.trim() to index
            }.toMap()
        } catch (e: Exception) {
            Log.e("SimpleTokenizer", "加载词表失败", e)
            emptyMap()
        }
    }

    /**
     * 释放资源
     */
    fun release() {
        // 清理缓存
    }
}
