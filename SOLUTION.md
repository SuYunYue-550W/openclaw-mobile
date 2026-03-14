# OpenClaw Android - 构建失败解决方案

## 🚨 SSL 证书错误修复

看到错误：`unable to find valid certification path`

这是因为 Gradle 无法验证 HTTPS 服务器的证书。

---

## ✅ 解决方案（按顺序尝试）

### 方案 1：使用已修复的构建脚本

```powershell
# 以管理员身份运行 PowerShell
cd C:\Projects\OpenClaw-Android

# 1. 先修复 SSL 证书
.\fix-ssl.ps1

# 2. 然后构建
.\build-fixed.ps1
```

---

### 方案 2：手动下载 Gradle（推荐）

#### 步骤 1：下载 Gradle 8.6

从以下任一镜像下载：

**腾讯镜像：**
```
https://mirrors.cloud.tencent.com/gradle/gradle-8.6-bin.zip
```

**阿里镜像：**
```
https://maven.aliyun.com/gradle/gradle-8.6-bin.zip
```

#### 步骤 2：解压

解压到：
```
D:\gradle-8.6\
```

#### 步骤 3：设置环境变量

```powershell
$env:GRADLE_HOME = "D:\gradle-8.6"
$env:Path = "$env:GRADLE_HOME\bin;$env:Path"
[Environment]::SetEnvironmentVariable("GRADLE_HOME", $env:GRADLE_HOME, "User")
[Environment]::SetEnvironmentVariable("Path", "$env:Path", "User")
```

#### 步骤 4：修改项目配置

编辑 `gradle\wrapper\gradle-wrapper.properties`:
```properties
distributionUrl=file\:///D:/gradle-8.6/bin/gradle-8.6-bin.zip
```

#### 步骤 5：构建

```powershell
cd C:\Projects\OpenClaw-Android
gradle assembleDebug
```

---

### 方案 3：使用 Android Studio（最简单）

1. **打开 Android Studio**

2. **打开项目**
   ```
   File → Open → C:\Projects\OpenClaw-Android
   ```

3. **等待 Gradle 同步**
   - Android Studio 会自动处理 SSL 问题
   - 首次同步约 5-10 分钟

4. **构建 APK**
   ```
   Build → Build Bundle(s) / APK(s) → Build APK(s)
   ```

---

### 方案 4：禁用 SSL 验证（不推荐用于生产）

编辑 `gradle.properties`，添加：
```properties
org.gradle.internal.http.connectionTimeout=180000
org.gradle.internal.http.socketTimeout=180000
org.gradle.internal.repository.initial.backoff=500
org.gradle.internal.repository.max.retries=10

# 禁用 SSL 验证（仅用于测试）
systemProp.javax.net.ssl.trustStoreType=WINDOWS-ROOT
systemProp.javax.net.ssl.trustAllCerts=true
```

---

## 🔧 其他常见问题

### 问题 1：找不到 Java

**解决：**
```powershell
$env:JAVA_HOME = "D:\jdk-17.0.9.8-hotspot"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
```

### 问题 2：找不到 Android SDK

**解决：**
创建 `local.properties`:
```properties
sdk.dir=D\:\\Android\\SDK
ndk.dir=D\:\\Android\\SDK\\ndk\\25.2.9519653
```

### 问题 3：Gradle 同步失败

**解决：**
```powershell
# 停止 Gradle 守护进程
.\gradlew.bat --stop

# 删除缓存
Remove-Item -Recurse -Force .gradle
Remove-Item -Recurse -Force app\build

# 重新构建
.\gradlew.bat assembleDebug --refresh-dependencies
```

---

## 📞 快速诊断命令

```powershell
# 检查 Java
java -version

# 检查 Gradle
.\gradlew.bat --version

# 检查 Android SDK
Test-Path "D:\Android\SDK"

# 测试网络连接
Test-NetConnection mirrors.cloud.tencent.com -Port 443

# 查看 Gradle 缓存位置
echo $env:GRADLE_USER_HOME
```

---

## 🎯 推荐方案

**最快方案：** 使用 Android Studio  
**最可靠方案：** 手动下载 Gradle  
**最自动化方案：** 运行 `fix-ssl.ps1` + `build-fixed.ps1`

---

**最后更新：** 2026-03-14  
**文档：** QUICK_FIX.md
