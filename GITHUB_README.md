# OpenClaw Mobile - Android 大模型助手

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Android CI](https://github.com/openclaw/openclaw-mobile/actions/workflows/android-ci.yml/badge.svg)](https://github.com/openclaw/openclaw-mobile/actions/workflows/android-ci.yml)
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com/)
[![API](https://img.shields.io/badge/API-28%2B-brightgreen.svg)](https://android-arsenal.com/api?level=28)

🤖 **OpenClaw Mobile** - 安全、私密的 Android 大模型聊天应用，支持本地部署和云端 API

> 这是 OpenClaw 项目的 Android 移动版本，让你随时随地使用大模型能力。

## ✨ 核心特性

### 🔐 安全性第一
- ✅ **AES-256 硬件级加密** - 使用 Android Keystore + Google Tink
- ✅ **动态权限管理** - 最小权限原则，按需申请
- ✅ **HTTPS + 证书绑定** - 防止中间人攻击
- ✅ **数据不上传云端** - 所有数据本地存储
- ✅ **ProGuard 代码混淆** - 保护应用逻辑

### ☁️ 云端 API 支持
支持国内主流大模型厂商：
- 🟢 **阿里云 DashScope** - 通义千问系列 (qwen-turbo/plus/max)
- 🟢 **智谱 AI** - ChatGLM Pro
- 🟢 **百度文心一言** - ERNIE Bot 4.0
- 🟢 **讯飞星火** - 认知大模型
- 🟢 **零一万物** - Yi-34B
- 🟢 **月之暗面** - Kimi

### 🏠 本地模型部署
- 🟢 **Qwen2.5-1.5B/3B** - 阿里云轻量级模型
- 🟢 **ChatGLM3-6B** - 智谱经典模型
- 🟢 **MiniCPM-2B** - 面壁智能高效模型
- 🟢 **ONNX Runtime 推理** - 跨平台推理引擎
- 🟢 **断点续传下载** - 大文件下载不中断

### 🎨 现代化 UI
- 🟢 **Material 3 设计** - 遵循最新 Material Design
- 🟢 **Jetpack Compose** - 声明式 UI 框架
- 🟢 **深色模式** - 自动适配系统主题
- 🟢 **动态取色** - Android 12+ Material You

## 📸 界面预览

<div align="center">
  <img src="screenshots/chat.png" width="200" alt="聊天界面"/>
  <img src="screenshots/models.png" width="200" alt="模型管理"/>
  <img src="screenshots/settings.png" width="200" alt="设置界面"/>
</div>

## 🚀 快速开始

### 环境要求
- **Android Studio** Hedgehog (2024.1) 或更高版本
- **JDK** 17+
- **Android SDK** 34
- **NDK** 25+ (用于本地推理，可选)

### 1. 克隆项目
```bash
git clone https://github.com/openclaw/openclaw-mobile.git
cd openclaw-mobile
```

### 2. 构建项目
```bash
# 使用 Android Studio 打开项目
# 或命令行构建
./gradlew assembleDebug

# 构建 Release 版本
./gradlew assembleRelease
```

### 3. 安装 APK
```bash
# 通过 ADB 安装
adb install app/build/outputs/apk/debug/app-debug.apk

# 或直接在 Android Studio 中运行
```

## 📖 详细文档

| 文档 | 说明 |
|------|------|
| [📱 项目说明](README.md) | 项目介绍和快速开始 |
| [🔐 安全架构](SECURITY.md) | 安全设计和实现细节 |
| [🏗️ 构建指南](BUILD_GUIDE.md) | 详细构建步骤和常见问题 |
| [📝 贡献指南](CONTRIBUTING.md) | 如何贡献代码 |
| [🐛 问题报告](.github/ISSUE_TEMPLATE/bug_report.md) | 提交 Bug |

## 🏗️ 技术架构

```
app/
├── src/main/java/com/openclaw/mobile/
│   ├── di/                    # 依赖注入 (Hilt)
│   │   └── AppModule.kt
│   ├── api/                   # API 层
│   │   ├── Models.kt          # 数据模型
│   │   └── ModelRepository.kt # API 仓库
│   ├── inference/             # 本地推理
│   │   └── LocalInferenceEngine.kt
│   ├── security/              # 安全模块
│   │   ├── SecureStorage.kt   # 加密存储
│   │   └── PermissionManager.kt
│   ├── service/               # 服务层
│   │   └── ModelDownloadService.kt
│   ├── viewmodel/             # ViewModel
│   │   └── ViewModels.kt
│   └── ui/                    # UI 层 (Jetpack Compose)
│       ├── MainActivity.kt
│       ├── OpenClawApp.kt
│       ├── ChatScreen.kt
│       ├── ModelsScreen.kt
│       ├── SettingsScreen.kt
│       └── ApiConfigScreen.kt
└── res/                       # 资源文件
```

### 技术栈

| 类别 | 技术 |
|------|------|
| **架构** | MVVM + Clean Architecture |
| **DI** | Hilt |
| **网络** | Retrofit + OkHttp |
| **本地存储** | Room + DataStore |
| **UI** | Jetpack Compose + Material 3 |
| **加密** | Android Keystore + Google Tink |
| **推理** | ONNX Runtime |

## 🔧 配置指南

### 配置 API Key

1. 打开应用 → 设置 → API Key 管理
2. 选择你的模型提供商
3. 输入 API Key（加密存储）
4. 点击保存

### 下载本地模型

1. 打开应用 → 模型
2. 切换到"本地模型"标签
3. 点击"下载"按钮
4. 等待下载完成（支持后台下载）

## 📱 Termux 集成 (高级用户)

OpenClaw Mobile 支持通过 Termux 实现更强大的功能：

### 安装 Termux 插件

```bash
# 在 Termux 中安装 Python 和依赖
pkg update && pkg upgrade
pkg install python
pip install openclaw-cli

# 配置 OpenClaw
openclaw configure
```

### 使用 Termux 运行本地模型

```bash
# 安装 llama.cpp
pkg install cmake
git clone https://github.com/ggerganov/llama.cpp
cd llama.cpp && make

# 运行模型
./main -m /sdcard/models/qwen2.5-1.5b.gguf -p "你好"
```

详细指南请查看 [TERMUX_GUIDE.md](TERMUX_GUIDE.md)

## 🤝 贡献

欢迎贡献代码、报告问题或提出建议！

### 开始贡献
1. Fork 项目
2. 创建分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

详细指南请查看 [贡献指南](CONTRIBUTING.md)

## 📄 开源协议

本项目采用 [MIT 协议](LICENSE) 开源

## 🙏 致谢

感谢以下开源项目：

- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Hilt](https://dagger.dev/hilt/)
- [ONNX Runtime](https://onnxruntime.ai/)
- [阿里云 DashScope](https://www.aliyun.com/product/dashscope)
- [ModelScope](https://modelscope.cn/)
- [OpenClaw](https://github.com/openclaw/openclaw)

## 📞 联系方式

- **GitHub Issues:** [提交问题](https://github.com/openclaw/openclaw-mobile/issues)
- **Discord:** [加入社区](https://discord.gg/clawd)
- **邮箱:** hello@openclaw.ai

## 🗺️ 路线图

### v1.0 (当前版本) ✅
- ✅ 基础聊天功能
- ✅ 云端 API 集成
- ✅ 安全加密存储
- ✅ Material 3 UI

### v1.1 (计划中)
- ⏳ 本地模型推理 (ONNX)
- ⏳ 语音输入/输出
- ⏳ 对话历史导出
- ⏳ 多账号管理

### v1.2 (计划中)
- ⏳ 图片理解 (VL 模型)
- ⏳ 多模态支持
- ⏳ 主题定制
- ⏳ 桌面小组件

## 📊 项目统计

- **代码行数:** ~6,500 行
- **Kotlin 文件:** 19 个
- **贡献者:** 1 人
- **创建日期:** 2026-03-14
- **最后更新:** 2026-03-15

---

<div align="center">

**Made with ❤️ by OpenClaw Team**

[⬆️ 返回顶部](#openclaw-mobile---android-大模型助手)

</div>
