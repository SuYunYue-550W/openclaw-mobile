# OpenClaw Mobile 构建指南

详细的构建步骤和常见问题解决方案。

## 📋 目录

1. [环境准备](#环境准备)
2. [克隆项目](#克隆项目)
3. [配置项目](#配置项目)
4. [构建 Debug 版本](#构建-debug-版本)
5. [构建 Release 版本](#构建-release-版本)
6. [签名配置](#签名配置)
7. [常见问题](#常见问题)

---

## 环境准备

### 必需软件

| 软件 | 版本 | 下载链接 |
|------|------|---------|
| Android Studio | Hedgehog (2024.1)+ | [下载](https://developer.android.com/studio) |
| JDK | 17+ | [下载](https://adoptium.net/) |
| Android SDK | 34 | Android Studio 内置 |
| NDK | 25+ (可选) | Android Studio SDK Manager |

### 环境变量配置

#### Windows

```powershell
# 添加到系统环境变量
ANDROID_HOME = C:\Users\<你的用户名>\AppData\Local\Android\Sdk
JAVA_HOME = C:\Program Files\Java\jdk-17
PATH = %PATH%;%ANDROID_HOME%\tools;%ANDROID_HOME%\platform-tools
```

#### macOS/Linux

```bash
# 添加到 ~/.bashrc 或 ~/.zshrc
export ANDROID_HOME=$HOME/Android/Sdk
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools
```

### 验证环境

```bash
# 检查 Java
java -version
# 应显示：java version "17.x.x"

# 检查 Android SDK
adb version
# 应显示：Android Debug Bridge version x.x.x

# 检查 Gradle
./gradlew --version
```

---

## 克隆项目

```bash
# 克隆仓库
git clone https://github.com/openclaw/openclaw-mobile.git
cd openclaw-mobile

# 拉取子模块（如果有）
git submodule update --init --recursive
```

---

## 配置项目

### 1. 同步 Gradle

使用 Android Studio:
- 打开项目
- 等待 Gradle 自动同步
- 或点击 `File > Sync Project with Gradle Files`

使用命令行:
```bash
./gradlew --refresh-dependencies
```

### 2. 配置本地属性

创建 `local.properties` 文件:

```properties
# Windows 示例
sdk.dir=C\:\\Users\\<你的用户名>\\AppData\\Local\\Android\\Sdk

# macOS 示例
sdk.dir=/Users/<你的用户名>/Library/Android/sdk

# Linux 示例
sdk.dir=/home/<你的用户名>/Android/Sdk
```

### 3. 配置签名（Release 版本需要）

创建 `keystore.properties`:

```properties
storePassword=your_store_password
keyPassword=your_key_password
keyAlias=your_key_alias
storeFile=../keystore.jks
```

---

## 构建 Debug 版本

### 使用 Android Studio

1. 选择 `Build > Build Bundle(s) / APK(s) > Build APK(s)`
2. 等待构建完成
3. APK 位置：`app/build/outputs/apk/debug/app-debug.apk`

### 使用命令行

```bash
# Windows
gradlew assembleDebug

# macOS/Linux
./gradlew assembleDebug
```

输出位置:
```
app/build/outputs/apk/debug/app-debug.apk
```

### 安装到设备

```bash
# 通过 USB 连接设备
adb devices

# 安装 APK
adb install app/build/outputs/apk/debug/app-debug.apk

# 如果已安装，覆盖安装
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## 构建 Release 版本

### 1. 生成签名密钥

```bash
# 使用 keytool 生成
keytool -genkey -v -keystore keystore.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias openclaw

# 按提示输入：
# - 密钥库密码
# - 姓名
# - 组织名称
# - 城市
# - 省份
# - 国家代码
```

### 2. 配置签名

编辑 `app/build.gradle.kts`:

```kotlin
android {
    ...
    signingConfigs {
        create("release") {
            storeFile = file("../keystore.jks")
            storePassword = System.getenv("STORE_PASSWORD") ?: keystoreProperties["storePassword"] as String
            keyAlias = System.getenv("KEY_ALIAS") ?: keystoreProperties["keyAlias"] as String
            keyPassword = System.getenv("KEY_PASSWORD") ?: keystoreProperties["keyPassword"] as String
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

### 3. 构建 Release APK

```bash
# 命令行构建
./gradlew assembleRelease

# 输出位置
app/build/outputs/apk/release/app-release.apk
```

### 4. 验证 APK

```bash
# 验证签名
apksigner verify --verbose app/build/outputs/apk/release/app-release.apk

# 查看 APK 信息
aapt dump badging app/build/outputs/apk/release/app-release.apk
```

---

## 签名配置

### 使用环境变量（推荐用于 CI/CD）

```bash
# 设置环境变量
export STORE_PASSWORD=your_password
export KEY_PASSWORD=your_password
export KEY_ALIAS=your_alias

# 构建
./gradlew assembleRelease
```

### 使用 GitHub Actions

```yaml
# .github/workflows/release.yml
name: Release Build

on:
  release:
    types: [created]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      
      - name: Decode Keystore
        run: echo ${{ secrets.KEYSTORE }} | base64 --decode > keystore.jks
      
      - name: Build Release APK
        run: ./gradlew assembleRelease
        env:
          STORE_PASSWORD: ${{ secrets.STORE_PASSWORD }}
          KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}
          KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
      
      - name: Upload APK
        uses: actions/upload-artifact@v3
        with:
          name: app-release
          path: app/build/outputs/apk/release/app-release.apk
```

---

## 常见问题

### Q1: Gradle 同步失败

**错误:** `Could not resolve all dependencies`

**解决方案:**
```bash
# 清除 Gradle 缓存
./gradlew cleanBuildCache

# 删除 .gradle 目录
rm -rf .gradle

# 重新同步
./gradlew --refresh-dependencies
```

### Q2: SDK 版本不匹配

**错误:** `failed to find target with hash string 'android-34'`

**解决方案:**
1. 打开 Android Studio
2. `Tools > SDK Manager`
3. 安装 Android 34 (API 34)
4. 重新同步

### Q3: 内存不足

**错误:** `Java heap space` 或 `GC overhead limit exceeded`

**解决方案:**
编辑 `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4096m -XX:MaxMetaspaceSize=1024m
```

### Q4: NDK 未找到

**错误:** `NDK is missing a "platforms" directory`

**解决方案:**
1. 打开 SDK Manager
2. 安装 NDK (Side by side) 25.x
3. 在 `local.properties` 添加:
```properties
ndk.dir=C\:\\Users\\<用户名>\\AppData\\Local\\Android\\Sdk\\ndk\\25.2.9519653
```

### Q5: 构建速度慢

**优化方案:**
```properties
# gradle.properties
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.daemon=true
org.gradle.configureondemand=true
```

### Q6: ProGuard 混淆后崩溃

**解决方案:**
编辑 `proguard-rules.pro`:
```proguard
# 保留数据类
-keep class com.openclaw.mobile.api.** { *; }
-keep class com.openclaw.mobile.viewmodel.** { *; }

# 保留 Gson 序列化
-keepattributes Signature
-keepattributes *Annotation*
```

### Q7: Hilt 注入失败

**错误:** `@HiltAndroidApp` 未处理

**解决方案:**
1. 确保 `OpenClawApplication` 正确注解
2. 检查 `build.gradle.kts` 中 KSP 配置
3. 清理并重建:
```bash
./gradlew clean
./gradlew build
```

### Q8: Compose 编译器错误

**错误:** `Compose Compiler requires Kotlin 1.9.0 or higher`

**解决方案:**
检查版本匹配:
```kotlin
// build.gradle.kts (project)
id("org.jetbrains.kotlin.android") version "1.9.23"

// build.gradle.kts (app)
composeOptions {
    kotlinCompilerExtensionVersion = "1.5.13"  // 对应 Kotlin 1.9.23
}
```

---

## 性能优化

### 启用构建缓存

```properties
# gradle.properties
org.gradle.caching=true
org.gradle.parallel=true
```

### 配置 R8 优化

```kotlin
// app/build.gradle.kts
android {
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}
```

### 减少 APK 大小

```kotlin
android {
    defaultConfig {
        // 只保留需要的语言
        resConfigs("zh", "en")
        
        // 只保留需要的 ABI
        ndk {
            abiFilters += listOf("arm64-v8a", "armeabi-v7a")
        }
    }
}
```

---

## 调试技巧

### 启用详细日志

```kotlin
// BuildConfig.DEBUG 自动在 Debug 版本为 true
if (BuildConfig.DEBUG) {
    HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
}
```

### 使用 Layout Inspector

1. 运行应用
2. `Tools > Layout Inspector`
3. 检查 UI 层次结构

### 性能分析

1. `Tools > Profiler`
2. 监控 CPU、内存、网络
3. 识别性能瓶颈

---

## 持续集成

### GitHub Actions 配置

```yaml
# .github/workflows/android-ci.yml
name: Android CI

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Grant execute permission for gradlew
      run: chmod +x gradlew
    
    - name: Build with Gradle
      run: ./gradlew build
    
    - name: Run tests
      run: ./gradlew test
    
    - name: Upload APK
      uses: actions/upload-artifact@v3
      with:
        name: app-debug
        path: app/build/outputs/apk/debug/app-debug.apk
```

---

## 参考资源

- [Android 开发者文档](https://developer.android.com/)
- [Gradle 用户指南](https://docs.gradle.org/)
- [Jetpack Compose 文档](https://developer.android.com/jetpack/compose)
- [Hilt 使用指南](https://dagger.dev/hilt/)

---

<div align="center">

**构建愉快！🦞**

遇到问题？[提交 Issue](https://github.com/openclaw/openclaw-mobile/issues)

</div>
