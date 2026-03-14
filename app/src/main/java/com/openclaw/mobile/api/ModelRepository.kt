package com.openclaw.mobile.api

import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 阿里云 DashScope API 服务
 */
interface DashScopeApi {
    @POST("api/v1/services/aigc/text-generation/generation")
    suspend fun generate(
        @Header("Authorization") authorization: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Body request: QwenRequest
    ): Response<QwenResponse>
}

/**
 * 国内大模型 API 通用接口
 */
interface DomesticModelApi {
    @POST
    suspend fun chat(
        @Header("Authorization") authorization: String,
        @Body request: QwenRequest
    ): Response<QwenResponse>
}

/**
 * API 服务仓库
 */
@Singleton
class ModelRepository @Inject constructor(
    private val secureStorage: com.openclaw.mobile.security.SecureStorage
) {
    private val dashScopeApi: DashScopeApi by lazy {
        val client = createHttpClient()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://dashscope.aliyuncs.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(DashScopeApi::class.java)
    }

    private fun createHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    /**
     * 调用阿里云通义千问
     */
    suspend fun callQwen(
        messages: List<Message>,
        model: QwenModel = QwenModel.QWEN_PLUS,
        stream: Boolean = true
    ): Result<QwenResponse> {
        return try {
            val apiKey = secureStorage.getApiKey("aliyun")
            if (apiKey.isNullOrBlank()) {
                return Result.failure(Exception("阿里云 API Key 未配置"))
            }

            val request = QwenRequest(
                model = model.id,
                input = Input(messages),
                parameters = Parameters(
                    maxTokens = 2048,
                    temperature = 0.7f
                ),
                stream = stream
            )

            val response = dashScopeApi.generate("Bearer $apiKey", request = request)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("响应为空"))
            } else {
                Result.failure(Exception("API 错误：${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 调用国内其他大模型
     */
    suspend fun callDomesticModel(
        model: DomesticModelConfig,
        messages: List<Message>,
        apiKey: String
    ): Result<QwenResponse> {
        return try {
            val client = createHttpClient()
            val retrofit = Retrofit.Builder()
                .baseUrl(model.baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val api = retrofit.create(DomesticModelApi::class.java)

            val request = QwenRequest(
                model = model.id,
                input = Input(messages),
                parameters = Parameters(),
                stream = false
            )

            val response = api.chat("Bearer $apiKey", request)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("响应为空"))
            } else {
                Result.failure(Exception("API 错误：${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 保存 API Key
     */
    suspend fun saveApiKey(provider: String, apiKey: String): Result<Unit> {
        return secureStorage.saveApiKey(provider, apiKey)
    }

    /**
     * 获取 API Key
     */
    suspend fun getApiKey(provider: String): String? {
        return secureStorage.getApiKey(provider)
    }
}
