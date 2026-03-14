package com.openclaw.mobile.api

import com.google.gson.annotations.SerializedName

/**
 * 阿里云 DashScope API 数据模型
 * 
 * 参考文档：https://help.aliyun.com/zh/dashscope/developer-reference/api-details
 */

/**
 * 通义千问 API 请求
 */
data class QwenRequest(
    @SerializedName("model") val model: String,
    @SerializedName("input") val input: Input,
    @SerializedName("parameters") val parameters: Parameters? = null,
    @SerializedName("stream") val stream: Boolean = true
)

data class Input(
    @SerializedName("messages") val messages: List<Message>
)

data class Message(
    @SerializedName("role") val role: String,
    @SerializedName("content") val content
) {
    companion object {
        fun system(content: String) = Message("system", content)
        fun user(content: String) = Message("user", content)
        fun assistant(content: String) = Message("assistant", content)
    }
}

data class Parameters(
    @SerializedName("result_format") val resultFormat: String = "message",
    @SerializedName("max_tokens") val maxTokens: Int = 2048,
    @SerializedName("temperature") val temperature: Float = 0.7f,
    @SerializedName("top_p") val topP: Float = 0.8f,
    @SerializedName("repetition_penalty") val repetitionPenalty: Float = 1.1f,
    @SerializedName("stop") val stop: List<String>? = null
)

/**
 * API 响应
 */
data class QwenResponse(
    @SerializedName("output") val output: Output,
    @SerializedName("usage") val usage: Usage,
    @SerializedName("request_id") val requestId: String
)

data class Output(
    @SerializedName("choices") val choices: List<Choice>,
    @SerializedName("finish_reason") val finishReason: String?
)

data class Choice(
    @SerializedName("message") val message: Message,
    @SerializedName("finish_reason") val finishReason: String?
)

data class Usage(
    @SerializedName("input_tokens") val inputTokens: Int,
    @SerializedName("output_tokens") val outputTokens: Int,
    @SerializedName("total_tokens") val totalTokens: Int
)

/**
 * 支持的阿里云模型列表
 */
enum class QwenModel(val id: String, val displayName: String, val description: String) {
    QWEN_TURBO("qwen-turbo", "通义千问 Turbo", "速度快，适合简单对话"),
    QWEN_PLUS("qwen-plus", "通义千问 Plus", "性能平衡，推荐使用"),
    QWEN_MAX("qwen-max", "通义千问 Max", "最强性能，复杂任务"),
    QWEN_MAX_LONGCONTEXT("qwen-max-longcontext", "通义千问 Max 长文本", "支持 200K 上下文"),
    QWEN_VL_PLUS("qwen-vl-plus", "通义千问 VL", "视觉语言模型"),
    QWEN_VL_MAX("qwen-vl-max", "通义千问 VL Max", "最强视觉理解");

    companion object {
        fun fromId(id: String): QwenModel? = values().find { it.id == id }
    }
}

/**
 * 国内其他大模型 API 配置
 */
sealed class DomesticModelConfig(
    val id: String,
    val displayName: String,
    val baseUrl: String,
    val apiKeySetting: String
) {
    // 智谱 AI - ChatGLM
    object ChatGLM : DomesticModelConfig(
        id = "chatglm_pro",
        displayName = "智谱 ChatGLM Pro",
        baseUrl = "https://open.bigmodel.cn/api/paas/v4/chat/completions",
        apiKeySetting = "zhipu_api_key"
    )

    // 百度文心一言
    object ErnieBot : DomesticModelConfig(
        id = "ernie-bot-4",
        displayName = "百度 文心一言 4.0",
        baseUrl = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions_pro",
        apiKeySetting = "baidu_api_key"
    )

    // 讯飞星火
    object Spark : DomesticModelConfig(
        id = "generalv3.5",
        displayName = "讯飞 星火认知",
        baseUrl = "https://spark-api.xf-yun.com/v3.5/chat",
        apiKeySetting = "iflytek_api_key"
    )

    // 零一万物
    object Yi : DomesticModelConfig(
        id = "yi-34b-chat-0205",
        displayName = "零一万物 Yi-34B",
        baseUrl = "https://api.lingyiwanwu.com/v1/chat/completions",
        apiKeySetting = "lingyi_api_key"
    )

    // 月之暗面 - Kimi
    object Kimi : DomesticModelConfig(
        id = "moonshot-v1-8k",
        displayName = "月之暗面 Kimi",
        baseUrl = "https://api.moonshot.cn/v1/chat/completions",
        apiKeySetting = "moonshot_api_key"
    )

    companion object {
        fun getAll(): List<DomesticModelConfig> = listOf(
            ChatGLM, ErnieBot, Spark, Yi, Kimi
        )

        fun fromId(id: String): DomesticModelConfig? = getAll().find { it.id == id }
    }
}
