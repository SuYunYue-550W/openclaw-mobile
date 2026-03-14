# OpenClaw Android 快速修复指南

## SSL 证书错误解决方案

### 问题原因
Gradle 无法验证 HTTPS 连接的 SSL 证书，通常是因为：
1. 公司/学校网络代理
2. Java 证书库过期
3. 网络连接问题

### 解决方案 1：使用国内镜像（推荐）

已更新配置文件使用阿里云镜像，直接运行：

```powershell
cd C:\Projects\OpenClaw-Android
.\build-fixed.ps1
```

### 解决方案 2：手动下载 Gradle

1. **下载 Gradle 8.6**
   ```
   https://mirrors.cloud.tencent.com/gradle/gradle-8.6-bin.zip
   ```

2. **解压到本地**
   ```
   D:\gradle-8.6\
   ```

3. **设置环境变量**
   ```powershell
   $env:GRADLE_HOME = "D:\gradle-8.6"
   $env:Path = "$env:GRADLE_HOME\bin;$env:Path"
   ```

4. **修改 wrapper 配置**
   编辑 `gradle\wrapper\gradle-wrapper.properties`:
   ```properties
   distributionUrl=file\:/D:/gradle-8.6/bin/gradle-8.6-bin.zip
   ```

### 解决方案 3：更新 Java 证书

```powershell
# 以管理员身份运行 PowerShell
keytool -importcert -file <cert_file> -keystore "$env:JAVA_HOME\lib\security\cacerts" -alias gradle
```

### 解决方案 4：使用 Android Studio（最简单）

1. 打开 Android Studio
2. File → Open → 选择项目
3. 等待自动同步
4. Build → Build APK

---

## 快速测试命令

```powershell
# 设置环境
$env:JAVA_HOME = "D:\jdk-17.0.9.8-hotspot"
$env:ANDROID_HOME = "D:\Android\SDK"

# 测试 Java
java -version

# 测试 Gradle wrapper
.\gradlew.bat --version

# 清理并构建
.\gradlew.bat clean assembleDebug --no-daemon
```

---

## 如果还是失败

1. **检查网络**
   ```powershell
   ping mirrors.cloud.tencent.com
   ```

2. **停止 Gradle 守护进程**
   ```powershell
   .\gradlew.bat --stop
   ```

3. **删除 Gradle 缓存**
   ```powershell
   Remove-Item -Recurse -Force .gradle
   Remove-Item -Recurse -Force app\build
   Remove-Item -Recurse -Force build
   ```

4. **使用离线模式**
   ```powershell
   .\gradlew.bat assembleDebug --offline
   ```

---

**最后更新：** 2026-03-14
