# OpenClaw Android 打包指南

## 📦 构建变体

### Debug 版本
- 包含调试信息
- 不启用代码混淆
- 可用于日常开发测试

### Release 版本
- 启用 ProGuard 混淆
- 启用资源压缩
- 签名后发布

---

## 🔑 配置签名

### 1. 生成签名密钥

```powershell
# 在项目中执行
cd C:\Projects\OpenClaw-Android

# 使用 keytool 生成密钥库
keytool -genkey -v -keystore openclaw-release-key.keystore -alias openclaw -keyalg RSA -keysize 2048 -validity 10000
```

按提示输入：
- 密钥库密码
- 姓名、组织、城市等信息

### 2. 创建 keystore.properties

在项目根目录创建 `keystore.properties`：

```properties
storePassword=你的密钥库密码
keyPassword=你的密钥密码
keyAlias=openclaw
storeFile=../openclaw-release-key.keystore
```

**注意：** 将此文件添加到 `.gitignore`，不要提交到版本控制！

### 3. 修改 build.gradle.kts

在 `app/build.gradle.kts` 的 `android` 块中添加：

```kotlin
android {
    // ... 其他配置
    
    signingConfigs {
        create("release") {
            val keystorePropertiesFile = rootProject.file("keystore.properties")
            val keystoreProperties = java.util.Properties()
            keystoreProperties.load(java.io.FileInputStream(keystorePropertiesFile))
            
            storePassword = keystoreProperties["storePassword"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            keyAlias = keystoreProperties["keyAlias"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
        }
    }
    
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

---

## 🏗️ 构建命令

### 命令行构建

```powershell
# 进入项目目录
cd C:\Projects\OpenClaw-Android

# 清理项目
gradlew clean

# 构建 Debug APK
gradlew assembleDebug

# 构建 Release APK
gradlew assembleRelease

# 构建 Release Bundle (用于 Google Play)
gradlew bundleRelease

# 安装到设备（需要连接设备）
gradlew installDebug
```

### Android Studio 构建

1. **Build → Generate Signed Bundle / APK**
2. 选择 **APK** 或 **Android App Bundle**
3. 选择密钥库文件
4. 输入密码
5. 选择 **release** 变体
6. 点击 **Finish**

---

## 📂 输出文件位置

```
app/build/outputs/apk/
├── debug/
│   └── app-debug.apk              # Debug 版本
└── release/
    ├── app-release.apk            # Release APK
    └── app-release-unaligned.apk  # 未对齐版本（不要使用）

app/build/outputs/bundle/
└── release/
    └── app-release.aab            # Google Play Bundle
```

---

## 📱 安装测试

### 安装到模拟器/真机

```powershell
# 使用 adb 安装
adb install app/build/outputs/apk/release/app-release.apk

# 如果已安装，覆盖安装
adb install -r app/build/outputs/apk/release/app-release.apk

# 卸载
adb uninstall com.openclaw.mobile
```

### 验证安装

```powershell
# 查看已安装的应用
adb shell pm list packages | grep openclaw

# 查看应用信息
adb shell dumpsys package com.openclaw.mobile
```

---

## 🔍 验证签名

```powershell
# 验证 APK 签名
apksigner verify --verbose app/build/outputs/apk/release/app-release.apk

# 查看签名信息
apksigner verify --print-certs app/build/outputs/apk/release/app-release.apk
```

---

## 📊 优化建议

### 1. 启用 R8 全模式

在 `gradle.properties` 中：
```properties
android.enableR8.fullMode=true
```

### 2. 启用构建缓存

```properties
org.gradle.caching=true
android.enableBuildCache=true
```

### 3. 增加 Gradle 内存

```properties
org.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8
```

### 4. 并行构建

```properties
org.gradle.parallel=true
```

---

## 🚨 常见问题

### Q1: 构建失败 - "SDK not found"
**解决：** 在 `local.properties` 中正确配置 SDK 路径
```properties
sdk.dir=D:\\Android\\SDK
```

### Q2: 签名失败 - "Keystore was tampered with, or password was incorrect"
**解决：** 检查密码是否正确，或重新生成密钥库

### Q3: ProGuard 导致崩溃
**解决：** 在 `proguard-rules.pro` 中添加保留规则
```proguard
-keep class com.openclaw.mobile.** { *; }
```

### Q4: APK 太大
**解决：**
1. 启用资源压缩：`isShrinkResources = true`
2. 使用 Android App Bundle (.aab)
3. 拆分 APK (按 ABI 或屏幕密度)

---

## 📦 发布到应用商店

### Google Play

1. 构建 AAB 文件：
   ```powershell
   gradlew bundleRelease
   ```

2. 上传到 [Google Play Console](https://play.google.com/console)

3. 填写应用信息、截图、隐私政策等

### 国内应用商店

1. 构建 APK：
   ```powershell
   gradlew assembleRelease
   ```

2. 准备材料：
   - 应用截图
   - 应用描述
   - 隐私政策
   - ICP 备案（部分商店需要）

3. 提交到各商店：
   - 华为应用市场
   - 小米应用商店
   - OPPO 软件商店
   - vivo 应用商店
   - 应用宝

---

## 🔐 安全注意事项

1. **保护密钥库文件**
   - 不要提交到 Git
   - 备份到安全位置
   - 使用强密码

2. **API Key 管理**
   - 不要硬编码在代码中
   - 使用加密存储
   - 考虑使用后端代理

3. **签名密钥备份**
   - 丢失后无法更新应用
   - 建议多重备份（本地 + 云端 + 物理介质）

---

## 📈 性能监控

### APK 大小分析

```powershell
# 使用 Android Studio
Build → Analyze APK...

# 选择生成的 APK 文件
# 查看各部分占用空间
```

### 方法数统计

```powershell
# 查看方法数（是否超过 64K）
gradlew app:dependencies
```

---

**最后更新：** 2026-03-14  
**文档版本：** 1.0
