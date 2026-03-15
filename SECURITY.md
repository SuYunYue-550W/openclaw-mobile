# OpenClaw Mobile 安全架构

本文档详细说明 OpenClaw Mobile 的安全设计和实现。

## 📋 目录

1. [安全原则](#安全原则)
2. [数据加密](#数据加密)
3. [权限管理](#权限管理)
4. [网络安全](#网络安全)
5. [代码安全](#代码安全)
6. [威胁模型](#威胁模型)

---

## 安全原则

### 最小权限原则

- 只申请必要的权限
- 权限按需动态申请
- 提供权限使用说明

### 数据本地化

- 所有敏感数据本地存储
- 不上传用户对话到任何服务器
- API Key 加密存储

### 防御深度

- 多层加密保护
- 代码混淆
- 运行时检查

---

## 数据加密

### 加密架构

```
┌─────────────────────────────────────────────────────────┐
│                    Android Keystore                      │
│              (硬件级密钥存储，TEE 保护)                    │
└─────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│                   Master Key (AES-256)                   │
│              (由 Keystore 生成和保护)                       │
└─────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│              EncryptedSharedPreferences                  │
│         (存储 API Key、设置等敏感数据)                      │
└─────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│                  Google Tink AEAD                        │
│         (额外的加密层，支持密钥轮换)                        │
└─────────────────────────────────────────────────────────┘
```

### 实现细节

#### 1. Android Keystore

```kotlin
val masterKey = MasterKey.Builder(context)
    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
    .build()
```

- 密钥由硬件生成和存储
- 密钥不可导出
- 设备锁定后无法访问

#### 2. EncryptedSharedPreferences

```kotlin
val encryptedPrefs = EncryptedSharedPreferences.create(
    context,
    "secure_preferences",
    masterKey,
    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
)
```

- Key 使用 AES-256-SIV 加密
- Value 使用 AES-256-GCM 加密
- 自动处理密钥派生和加密

#### 3. Google Tink

```kotlin
val keysetHandle = KeysetHandle.generateNew(KeyTemplates.get("AES256_GCM"))
val aead = AeadFactory.getPrimitive(keysetHandle)
val encrypted = aead.encrypt(data.toByteArray(), null)
```

- 提供额外的加密层
- 支持密钥轮换
- 防止某些类型的攻击

### 加密数据清单

| 数据类型 | 加密方式 | 存储位置 |
|---------|---------|---------|
| API Key | Keystore + Tink | EncryptedSharedPreferences |
| 对话历史 | Keystore | EncryptedSharedPreferences |
| 用户设置 | Keystore | EncryptedSharedPreferences |
| 模型文件 | 无（公开数据） | 外部存储 |

---

## 权限管理

### 权限分类

#### 必要权限

| 权限 | 用途 | 是否必需 |
|------|------|---------|
| `INTERNET` | 调用云端 API | ✅ |
| `ACCESS_NETWORK_STATE` | 检查网络状态 | ✅ |
| `FOREGROUND_SERVICE` | 后台下载模型 | ✅ |

#### 可选权限

| 权限 | 用途 | 何时申请 |
|------|------|---------|
| `READ_MEDIA_IMAGES` | 上传图片理解 | 用户使用图片功能时 |
| `POST_NOTIFICATIONS` | 显示下载进度 | Android 13+ 首次下载时 |
| `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | 后台推理 | 用户使用本地模型时 |

### 权限申请流程

```
用户触发功能
    │
    ▼
检查权限状态
    │
    ├── 已授予 ──▶ 执行功能
    │
    ├── 未申请 ──▶ 显示说明 ──▶ 申请权限
    │                              │
    │                              ├── 同意 ──▶ 执行功能
    │                              │
    │                              └── 拒绝 ──▶ 提示用户
    │
    └── 永久拒绝 ──▶ 引导至设置页面
```

### 实现代码

```kotlin
fun requestPermissions(
    activity: Activity,
    permissions: Array<String>,
    requestCode: Int
) {
    val permissionsToRequest = permissions.filter {
        ContextCompat.checkSelfPermission(activity, it) 
            != PackageManager.PERMISSION_GRANTED
    }.toTypedArray()

    if (permissionsToRequest.isNotEmpty()) {
        ActivityCompat.requestPermissions(
            activity, 
            permissionsToRequest, 
            requestCode
        )
    }
}
```

---

## 网络安全

### HTTPS 强制

所有网络通信强制使用 HTTPS：

```kotlin
val client = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .build()
```

### API Key 传输

```
应用 ──[加密存储]──> SharedPreferences
        │
        ▼
    使用时解密
        │
        ▼
   添加到 HTTP Header
        │
        ▼
   HTTPS 发送 ──> API 服务器
```

### 证书绑定（未来）

```kotlin
// TODO: 实现证书绑定
val certificatePinner = CertificatePinner.Builder()
    .add("dashscope.aliyuncs.com", "sha256/...")
    .build()
```

---

## 代码安全

### ProGuard 混淆

```proguard
# 启用混淆
-isMinifyEnabled true
-isShrinkResources true

# 保留必要类
-keep class com.openclaw.mobile.api.** { *; }
-keep class com.openclaw.mobile.security.** { *; }

# 混淆其他类
-dontwarn com.openclaw.mobile.**
```

### 敏感信息处理

```kotlin
// ❌ 错误：硬编码密钥
val apiKey = "sk-1234567890"

// ✅ 正确：从加密存储读取
val apiKey = secureStorage.getApiKey("aliyun")
```

### 日志安全

```kotlin
// ❌ 错误：记录敏感信息
Log.d("API", "Using key: $apiKey")

// ✅ 正确：不记录敏感信息
Log.d("API", "Request sent successfully")
```

---

## 威胁模型

### 威胁场景 1: 设备丢失

**威胁:** 攻击者获取设备，尝试访问应用数据

**缓解措施:**
- ✅ 数据加密存储（Keystore 保护）
- ✅ 设备锁定后密钥不可访问
- ✅ 可选的应用锁（未来）

**剩余风险:** 低

### 威胁场景 2: 恶意应用

**威胁:** 其他应用尝试读取 OpenClaw 的数据

**缓解措施:**
- ✅ Android 沙箱隔离
- ✅ 文件权限限制
- ✅ 加密数据存储

**剩余风险:** 极低

### 威胁场景 3: 中间人攻击

**威胁:** 攻击者拦截网络通信

**缓解措施:**
- ✅ 强制 HTTPS
- ⏳ 证书绑定（计划中）
- ✅ API Key 加密传输

**剩余风险:** 低

### 威胁场景 4: 恶意模型文件

**威胁:** 下载的模型文件被篡改

**缓解措施:**
- ✅ 从官方源下载
- ⏳ SHA256 校验（计划中）
- ⏳ 签名验证（计划中）

**剩余风险:** 中

---

## 安全清单

### 开发时

- [ ] 不硬编码密钥
- [ ] 不使用明文日志
- [ ] 启用 ProGuard 混淆
- [ ] 审查第三方依赖

### 发布前

- [ ] 安全审计
- [ ] 渗透测试
- [ ] 依赖漏洞扫描
- [ ] 权限审查

### 运行时

- [ ] 检测 Root 环境（未来）
- [ ] 检测模拟器（未来）
- [ ] 检测调试器（未来）
- [ ] 完整性校验（未来）

---

## 事件响应

### 发现安全漏洞

1. **立即行动:**
   - 评估影响范围
   - 制定修复方案
   - 准备补丁

2. **通知用户:**
   - 发布安全公告
   - 提供缓解措施
   - 推送更新

3. **事后分析:**
   - 根因分析
   - 流程改进
   - 文档更新

### 联系方式

发现安全问题请通过以下方式报告：

- **Email:** security@openclaw.ai
- **GitHub:** [Security Advisories](https://github.com/openclaw/openclaw-mobile/security/advisories)

---

## 参考资源

- [Android 安全最佳实践](https://developer.android.com/security/best-practices)
- [Google Tink 文档](https://developers.google.com/tink)
- [OWASP Mobile Top 10](https://owasp.org/www-project-mobile-top-10/)

---

<div align="center">

**安全是持续的过程，不是终点**

最后更新：2026-03-15

</div>
