# OpenClaw Mobile - 5 分钟快速开始

> 🚀 从零开始到运行应用，只需 5 分钟！

---

## ⚡ 方式一：Android Studio（推荐新手）

### 步骤 1：打开项目 (1 分钟)

1. 启动 Android Studio
2. 点击 **Open**
3. 选择文件夹：`C:\Projects\OpenClaw-Android`
4. 等待 Gradle 同步完成

### 步骤 2：连接设备 (1 分钟)

**使用真机：**
```bash
# 1. 手机开启开发者选项和 USB 调试
# 2. 用 USB 连接电脑
# 3. 验证连接
adb devices

# 应显示类似：
# List of devices attached
# ABC123456789    device
```

**使用模拟器：**
1. Android Studio → **Device Manager**
2. 创建或启动一个模拟器

### 步骤 3：运行应用 (1 分钟)

1. 点击顶部绿色 **Run** 按钮（或 `Shift + F10`）
2. 选择你的设备
3. 等待应用安装并启动

### 步骤 4：配置 API (1 分钟)

1. 应用启动后，点击底部 **设置**
2. 点击 **API Key 管理**
3. 输入你的 API Key（任选一个厂商）：
   - 阿里云：https://dashscope.console.aliyun.com/apiKey
   - 智谱：https://open.bigmodel.cn/usercenter/apikeys
   - 百度：https://cloud.baidu.com/doc/WENXINWORKSHOP/
4. 点击顶部 **✓** 保存

### 步骤 5：开始聊天 (1 分钟)

1. 点击底部 **聊天**
2. 输入消息："你好，介绍一下你自己"
3. 点击发送按钮
4. 等待回复...

**🎉 完成！你现在可以开始使用了！**

---

## ⚡ 方式二：命令行构建（推荐开发者）

### 前提条件

```bash
# 确保已安装
java -version      # 需要 JDK 17+
adb version        # 需要 Android SDK
```

### 一键构建并安装

```powershell
# Windows PowerShell
cd C:\Projects\OpenClaw-Android

# 构建 Debug 版本
.\gradlew assembleDebug

# 安装到设备
adb install app\build\outputs\apk\debug\app-debug.apk

# 启动应用
adb shell am start -n com.openclaw.mobile/.ui.MainActivity
```

```bash
# macOS/Linux
cd /path/to/OpenClaw-Android

./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.openclaw.mobile/.ui.MainActivity
```

---

## ⚡ 方式三：直接安装 APK（最简单）

如果你不想编译项目：

1. 从 [Releases](https://github.com/openclaw/openclaw-mobile/releases) 下载最新 APK
2. 传输到手机
3. 在手机上安装
4. 打开应用并配置 API Key

---

## 🔧 常见问题

### Q: Gradle 同步失败？

```bash
# 清除缓存并重试
.\gradlew cleanBuildCache
.\gradlew --refresh-dependencies
```

### Q: 找不到设备？

```bash
# 检查 USB 调试是否开启
adb kill-server
adb start-server
adb devices

# 如果还是没有设备：
# 1. 重新插拔 USB
# 2. 手机上允许 USB 调试
# 3. 尝试不同的 USB 端口
```

### Q: 应用闪退？

1. 查看日志：
```bash
adb logcat | findstr "openclaw"
```

2. 常见问题：
   - API Key 未配置 → 去设置里配置
   - 网络问题 → 检查网络连接
   - 权限问题 → 授予必要权限

### Q: 收不到回复？

1. 检查 API Key 是否正确
2. 检查网络连接
3. 查看日志：
```bash
adb logcat -s "OpenClaw"
```

---

## 📱 功能速览

### 聊天功能
- ✅ 发送文字消息
- ✅ 接收 AI 回复
- ✅ 显示加载状态
- ✅ 错误提示

### 模型管理
- ✅ 查看支持的云端模型
- ✅ 下载本地模型（高级）
- ✅ 切换模型

### 设置
- ✅ 配置 API Key（6 个厂商）
- ✅ 查看安全信息
- ✅ 清除数据

---

## 🎯 下一步

### 推荐配置

1. **设置默认模型**
   - 模型 → 云端 API → 选择你喜欢的模型
   - 推荐：通义千问 Plus（平衡性能和速度）

2. **配置多个 API Key**
   - 可以配置多个厂商的 Key
   - 随时切换使用

3. **探索 Termux 集成**
   - 阅读 [TERMUX_GUIDE.md](TERMUX_GUIDE.md)
   - 实现自动化脚本

### 学习资源

- [完整文档](README.md)
- [构建指南](BUILD_GUIDE.md)
- [安全架构](SECURITY.md)
- [Termux 指南](TERMUX_GUIDE.md)

---

## 📞 需要帮助？

- **GitHub Issues:** https://github.com/openclaw/openclaw-mobile/issues
- **Discord:** https://discord.gg/clawd
- **邮箱:** hello@openclaw.ai

---

<div align="center">

**祝你使用愉快！🦞**

*OpenClaw Mobile v1.0*

</div>
