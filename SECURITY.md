# OpenClaw Mobile 安全架构文档

## 1. 安全设计原则

### 1.1 最小权限原则
- 仅申请必要的权限
- 权限使用时明确告知用户
- 权限使用记录可审计

### 1.2 数据加密
- 所有敏感数据加密存储
- 传输层使用 TLS 1.3
- 密钥由硬件级 Keystore 保护

### 1.3 防御深度
- 多层安全防护
- 代码混淆
- 反逆向工程

## 2. 加密实现

### 2.1 密钥层级
```
Android Keystore (硬件级)
    └── Master Key (AES-256-GCM)
        └── EncryptedSharedPreferences
            └── Tink Keyset
                └── 数据加密密钥 (AEAD)
```

### 2.2 API Key 加密流程
```kotlin
// 1. 生成主密钥（Android Keystore）
val masterKey = MasterKey.Builder(context)
    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
    .build()

// 2. 创建加密 SharedPreferences
val encryptedPrefs = EncryptedSharedPreferences.create(
    context, "secure_preferences", masterKey, ...
)

// 3. 使用 Tink 加密数据
val keysetHandle = KeysetHandle.generateNew(KeyTemplates.get("AES256_GCM"))
val aead = AeadFactory.getPrimitive(keysetHandle)
val encrypted = aead.encrypt(apiKey.toByteArray(), null)
```

### 2.3 对话历史加密
```kotlin
// 使用 Room + SQLCipher 加密数据库
@Entity(tableName = "conversations")
data class Conversation(
    @PrimaryKey val id: String,
    val encryptedContent: String, // 加密后的内容
    val timestamp: Long
)

// 加密存储
val encrypted = aead.encrypt(conversationJson.toByteArray(), null)
```

## 3. 网络安全

### 3.1 证书绑定
```xml
<!-- network_security_config.xml -->
<domain-config>
    <domain includeSubdomains="true">dashscope.aliyuncs.com</domain>
    <pin-set expiration="2027-01-01">
        <pin digest="SHA-256">BASE64_CERT_HASH</pin>
    </pin-set>
</domain-config>
```

### 3.2 HTTPS 强制
```xml
<base-config cleartextTrafficPermitted="false">
    <trust-anchors>
        <certificates src="system" />
        <certificates src="user" />
    </trust-anchors>
</base-config>
```

### 3.3 OkHttp 安全配置
```kotlin
val client = OkHttpClient.Builder()
    .sslSocketFactory(sslContext.socketFactory, trustManager)
    .certificatePinner(CertificatePinner.Builder()
        .add("dashscope.aliyuncs.com", "sha256/xxx")
        .build())
    .build()
```

## 4. 权限管理

### 4.1 权限分类
| 类别 | 权限 | 风险等级 |
|------|------|----------|
| 网络 | INTERNET | 低 |
| 存储 | READ/WRITE_EXTERNAL_STORAGE | 高 |
| 通知 | POST_NOTIFICATIONS | 低 |
| 后台 | FOREGROUND_SERVICE | 中 |

### 4.2 动态申请流程
```
用户触发操作 → 检查权限 → 未授予 → 显示说明对话框
    → 用户同意 → 申请权限 → 记录日志
    → 用户拒绝 → 引导至设置
```

### 4.3 权限审计日志
```kotlin
data class PermissionLog(
    val permission: String,
    val timestamp: Long,
    val purpose: String,
    val granted: Boolean
)
```

## 5. 数据安全

### 5.1 云端备份排除
```xml
<!-- data_extraction_rules.xml -->
<cloud-backup>
    <exclude domain="sharedpref" path="secure_preferences.xml" />
    <exclude domain="database" path="conversations.db" />
    <exclude domain="file" path="models" />
</cloud-backup>
```

### 5.2 数据清除
```kotlin
suspend fun clearAllData() {
    // 清除加密数据
    secureStorage.clearAll()
    
    // 清除数据库
    conversationDao.deleteAll()
    
    // 清除模型文件
    modelsDir.deleteRecursively()
    
    // 清除缓存
    cacheDir.deleteRecursively()
}
```

## 6. 代码保护

### 6.1 ProGuard 混淆
```proguard
# 不混淆类名（可选，增加逆向难度）
-dontobfuscate

# 保留关键类
-keep class com.openclaw.mobile.security.** { *; }
-keep class com.google.crypto.tink.** { *; }
```

### 6.2 反调试
```kotlin
fun isDebuggerConnected(): Boolean {
    return Debug.isDebuggerConnected()
}

// 在关键代码前检查
if (isDebuggerConnected()) {
    // 退出或执行误导操作
    System.exit(0)
}
```

## 7. 安全审计清单

### 7.1 开发阶段
- [ ] 所有敏感数据加密
- [ ] 无硬编码密钥
- [ ] 使用 HTTPS
- [ ] 权限最小化
- [ ] 日志不包含敏感信息

### 7.2 发布前
- [ ] 启用 ProGuard
- [ ] 移除调试代码
- [ ] 证书绑定
- [ ] 安全测试
- [ ] 第三方依赖审计

### 7.3 运行时
- [ ] 检测 Root
- [ ] 检测调试器
- [ ] 检测模拟器（可选）
- [ ] 证书验证

## 8. 合规性

### 8.1 隐私政策
- 明确告知数据收集范围
- 说明数据使用目的
- 提供数据导出和删除功能

### 8.2 用户同意
- 首次启动显示隐私政策
- 敏感权限单独申请
- 可随时撤回同意

### 8.3 数据存储
- 本地存储优先
- 云端同步需用户授权
- 支持数据完全删除

## 9. 应急响应

### 9.1 密钥泄露
1. 远程吊销 API Key
2. 通知用户修改密码
3. 更新加密密钥

### 9.2 漏洞报告
- GitHub Security Advisories
- 安全邮件：security@openclaw.ai
- 48 小时内响应

---

*文档版本：1.0*
*最后更新：2026-03-14*
