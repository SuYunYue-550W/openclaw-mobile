# OpenClaw Mobile

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Android CI](https://github.com/openclaw/openclaw-mobile/actions/workflows/android-ci.yml/badge.svg)](https://github.com/openclaw/openclaw-mobile/actions/workflows/android-ci.yml)
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com/)
[![API](https://img.shields.io/badge/API-28%2B-brightgreen.svg)](https://android-arsenal.com/api?level=28)

🤖 **OpenClaw Mobile** - 安全、私密的 Android 大模型聊天应用，支持本地部署和云端 API

## ✨ 特性

### 🔐 安全性
- ✅ AES-256 硬件级加密
- ✅ 动态权限管理
- ✅ HTTPS + 证书绑定
- ✅ 数据不上传云端
- ✅ ProGuard 代码混淆

### ☁️ 云端 API 支持
- 🟢 阿里云 DashScope (通义千问)
- 🟢 智谱 AI (ChatGLM)
- 🟢 百度文心一言
- 🟢 讯飞星火
- 🟢 零一万物
- 🟢 月之暗面 Kimi

### 🏠 本地模型部署
- 🟢 Qwen2.5-1.5B/3B
- 🟢 ChatGLM3-6B
- 🟢 MiniCPM-2B
- 🟢 ONNX Runtime 推理
- 🟢 断点续传下载

### 🎨 用户体验
- 🟢 Material 3 设计
- 🟢 Jetpack Compose
- 🟢 深色模式
- 🟢 响应式布局

## 📸 截图

<div align="center">
  <img src="screenshots/chat.png" width="200" alt="聊天界面"/>
  <img src="screenshots/models.png" width="200" alt="模型管理"/>
  <img src="screenshots/settings.png" width="200" alt="设置界面"/>
</div>

## 🚀 快速开始

### 环境要求
- Android Studio Hedgehog (2024.1) 或更高版本
- JDK 17+
- Android SDK 34
- NDK 25+ (用于本地推理)

### 克隆项目
```bash
git clone https://github.com/openclaw/openclaw-mobile.git
cd openclaw-mobile
```

### 构建项目
```bash
# 使用 Android Studio 打开项目
# 或命令行构建
./gradlew assembleDebug
```

### 安装 APK
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## 📖 文档

- [📱 项目说明](README.md)
- [🔐 安全架构](SECURITY.md)
- [🏗️ 构建指南](BUILD_GUIDE.md)
- [📝 贡献指南](CONTRIBUTING.md)
- [🐛 问题报告](.github/ISSUE_TEMPLATE/bug_report.md)

## 🏗️ 架构

```
app/
├── src/main/java/com/openclaw/mobile/
│   ├── di/                    # 依赖注入 (Hilt)
│   ├── api/                   # API 层
│   │   ├── Models.kt          # 数据模型
│   │   └── ModelRepository.kt # API 仓库
│   ├── inference/             # 本地推理
│   │   ├── LocalInferenceEngine.kt
│   │   └── OnnxInferenceEngine.kt
│   ├── security/              # 安全模块
│   │   ├── SecureStorage.kt   # 加密存储
│   │   └── PermissionManager.kt
│   ├── service/               # 服务层
│   │   └── ModelDownloadService.kt
│   ├── viewmodel/             # ViewModel
│   └── ui/                    # UI 层 (Jetpack Compose)
└── res/                       # 资源文件
```

**技术栈：**
- **架构:** MVVM + Clean Architecture
- **DI:** Hilt
- **网络:** Retrofit + OkHttp
- **本地存储:** Room + DataStore
- **UI:** Jetpack Compose + Material 3
- **加密:** Android Keystore + Google Tink
- **推理:** ONNX Runtime

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

## 📞 联系方式

- **GitHub Issues:** [提交问题](https://github.com/openclaw/openclaw-mobile/issues)
- **Discord:** [加入社区](https://discord.gg/clawd)
- **邮箱:** hello@openclaw.ai

## 🗺️ 路线图

### v1.0 (当前版本)
- ✅ 基础聊天功能
- ✅ 云端 API 集成
- ✅ 安全加密存储
- ✅ Material 3 UI

### v1.1 (计划中)
- ⏳ 本地模型推理
- ⏳ 语音输入/输出
- ⏳ 对话历史导出

### v1.2 (计划中)
- ⏳ 图片理解
- ⏳ 多模态支持
- ⏳ 主题定制

## 📊 项目统计

- **代码行数:** ~6,300 行
- **Kotlin 文件:** 18 个
- **贡献者:** 1 人
- **创建日期:** 2026-03-14

---

<div align="center">

**Made with ❤️ by OpenClaw Team**

[⬆️ 返回顶部](#openclaw-mobile)

</div>
