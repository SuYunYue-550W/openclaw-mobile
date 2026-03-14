package com.openclaw.mobile.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.aead.AeadFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 安全加密存储管理器
 * 
 * 功能：
 * 1. 使用 Android Keystore 生成主密钥
 * 2. EncryptedSharedPreferences 存储敏感数据
 * 3. Tink 库进行数据加密
 * 4. API Key、对话历史等敏感信息加密存储
 */
@Singleton
class SecureStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val encryptedPrefs by lazy {
        EncryptedSharedPreferences.create(
            context,
            "secure_preferences",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private val keysetHandle: KeysetHandle by lazy {
        // 生成或加载 Tink 密钥
        val prefs = context.getSharedPreferences("tink_keyset", Context.MODE_PRIVATE)
        if (prefs.contains("keyset")) {
            // 从存储加载
            val keysetJson = prefs.getString("keyset", "") ?: ""
            if (keysetJson.isNotEmpty()) {
                return@lazy KeysetHandle.readWithNoSecrets(
                    com.google.crypto.tink.JsonKeysetReader.withString(keysetJson)
                )
            }
        }
        // 生成新密钥
        val newKeyset = KeysetHandle.generateNew(KeyTemplates.get("AES256_GCM"))
        prefs.edit()
            .putString("keyset", newKeyset.toString())
            .apply()
        newKeyset
    }

    private val aead: Aead by lazy {
        AeadConfig.register()
        AeadFactory.getPrimitive(keysetHandle)
    }

    /**
     * 保存 API Key（加密存储）
     */
    suspend fun saveApiKey(provider: String, apiKey: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val encryptedKey = aead.encrypt(apiKey.toByteArray(), null)
            val encodedKey = android.util.Base64.encodeToString(encryptedKey, android.util.Base64.DEFAULT)
            encryptedPrefs.edit()
                .putString("api_key_$provider", encodedKey)
                .apply()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 获取 API Key（解密）
     */
    suspend fun getApiKey(provider: String): String? = withContext(Dispatchers.IO) {
        try {
            val encodedKey = encryptedPrefs.getString("api_key_$provider", null) ?: return@withContext null
            val encryptedKey = android.util.Base64.decode(encodedKey, android.util.Base64.DEFAULT)
            val decrypted = aead.decrypt(encryptedKey, null)
            String(decrypted)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 删除 API Key
     */
    suspend fun deleteApiKey(provider: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            encryptedPrefs.edit()
                .remove("api_key_$provider")
                .apply()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 保存用户设置（非敏感）
     */
    suspend fun saveSetting(key: String, value: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            encryptedPrefs.edit()
                .putString("setting_$key", value)
                .apply()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 获取用户设置
     */
    suspend fun getSetting(key: String, defaultValue: String = ""): String = withContext(Dispatchers.IO) {
        encryptedPrefs.getString("setting_$key", defaultValue) ?: defaultValue
    }

    /**
     * 清除所有数据
     */
    suspend fun clearAll(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            encryptedPrefs.edit().clear().apply()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 加密任意数据
     */
    fun encryptData(data: String): String {
        val encrypted = aead.encrypt(data.toByteArray(), null)
        return android.util.Base64.encodeToString(encrypted, android.util.Base64.DEFAULT)
    }

    /**
     * 解密任意数据
     */
    fun decryptData(encryptedData: String): String {
        val encrypted = android.util.Base64.decode(encryptedData, android.util.Base64.DEFAULT)
        val decrypted = aead.decrypt(encrypted, null)
        return String(decrypted)
    }
}
