# OpenClaw Mobile 🦞

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Android CI](https://github.com/openclaw/openclaw-mobile/actions/workflows/android-ci.yml/badge.svg)](https://github.com/openclaw/openclaw-mobile/actions/workflows/android-ci.yml)
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com/)
[![API](https://img.shields.io/badge/API-28%2B-brightgreen.svg)](https://android-arsenal.com/api?level=28)

🤖 **安全、私密的 Android 大模型聊天应用** - 支持本地部署和云端 API

> OpenClaw 项目的 Android 移动版本，让你随时随地使用大模型能力。

---

## ✨ 特性

### 🔐 安全性
- ✅ AES-256 硬件级加密
- ✅ 动态权限管理
- ✅ HTTPS + 证书绑定
- ✅ 数据不上传云端

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

### 🎨 用户体验
- 🟢 Material 3 设计
- 🟢 Jetpack Compose
- 🟢 深色模式
- 🟢 响应式布局

---

## 🚀 快速开始

### 5 分钟上手

```bash
# 1. 克隆项目
git clone https://github.com/openclaw/openclaw-mobile.git
cd openclaw-mobile

# 2. 用 Android Studio 打开
# 3. 点击 Run 按钮
# 4. 配置 API Key
# 5. 开始聊天！
```

详细指南请查看 [**5 分钟快速开始**](QUICKSTART.md)

---

## 📸 界面预览

<div align="center">
  <img src="screenshots/chat.png" width="200" alt="聊天界面"/>
  <img src="screenshots/models.png" width="200" alt="模型管理"/>
  <img src="screenshots/settings.png" width="200" alt="设置界面"/>
</div>

---

## 📖 文档导航

| 文档 | 说明 | 适合人群 |
|------|------|---------|
| [⚡ 快速开始](QUICKSTART.md) | 5 分钟上手指南 | 所有用户 |
| [📱 项目说明](README.md) | 本文档 | 所有用户 |
| [🏗️ 构建指南](BUILD_GUIDE.md) | 详细构建步骤 | 开发者 |
| [🔐 安全架构](SECURITY.md) | 安全设计文档 | 安全研究人员 |
| [📟 Termux 指南](TERMUX_GUIDE.md) | Termux 集成 | 高级用户 |
| [📝 修复总结](FIXES_SUMMARY.md) | 错误修复记录 | 维护者 |

---

## 🏗️ 技术架构

```
┌─────────────────────────────────────────────────────────┐
│                    UI Layer (Compose)                    │
│  ChatScreen │ ModelsScreen │ SettingsScreen │ ApiConfig  │
└─────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│                 ViewModel Layer (Hilt)                   │
│        ChatViewModel │ ModelViewModel │ SettingsVM       │
└─────────────────────────────────────────────────────────┘
                            │
            ┌───────────────┴───────────────┐
            ▼                               ▼
┌─────────────────────┐         ┌─────────────────────┐
│   Repository Layer  │         │   Service Layer     │
│  ModelRepository    │         │ ModelDownloadService│
└─────────────────────┘         └─────────────────────┘
            │                               │
            ▼                               ▼
┌─────────────────────┐         ┌─────────────────────┐
│     API Layer       │         │  Local Inference    │
│  DashScope API      │         │  ONNX Runtime       │
│  Domestic Models    │         │  Model Storage      │
└─────────────────────┘         └─────────────────────┘
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

---

## 📦 安装

### 方式一：从 GitHub Releases 下载

1. 访问 [Releases](https://github.com/openclaw/openclaw-mobile/releases)
2. 下载最新 APK
3. 在手机上安装

### 方式二：自己构建

```bash
git clone https://github.com/openclaw/openclaw-mobile.git
cd openclaw-mobile
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

详细步骤请查看 [构建指南](BUILD_GUIDE.md)

---

## ⚙️ 配置

### 配置 API Key

1. 打开应用 → 设置 → API Key 管理
2. 选择你的模型提供商
3. 输入 API Key
4. 点击保存

### 获取 API Key

| 厂商 | 链接 |
|------|------|
| 阿里云 | https://dashscope.console.aliyun.com/apiKey |
| 智谱 AI | https://open.bigmodel.cn/usercenter/apikeys |
| 百度 | https://cloud.baidu.com/doc/WENXINWORKSHOP/ |
| 讯飞 | https://console.xfyun.cn/services/cbm |
| 零一万物 | https://platform.lingyiwanwu.com/apikeys |
| 月之暗面 | https://platform.moonshot.cn/console/api-keys |

---

## 🤝 贡献

欢迎贡献代码、报告问题或提出建议！

### 开始贡献

1. Fork 项目
2. 创建分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

详细指南请查看 [贡献指南](CONTRIBUTING.md)

---

## 📄 开源协议

本项目采用 [MIT 协议](LICENSE) 开源

---

## 🙏 致谢

感谢以下开源项目：

- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Hilt](https://dagger.dev/hilt/)
- [ONNX Runtime](https://onnxruntime.ai/)
- [阿里云 DashScope](https://www.aliyun.com/product/dashscope)
- [ModelScope](https://modelscope.cn/)
- [OpenClaw](https://github.com/openclaw/openclaw)

---

## 📞 联系方式

- **GitHub Issues:** [提交问题](https://github.com/openclaw/openclaw-mobile/issues)
- **Discord:** [加入社区](https://discord.gg/clawd)
- **邮箱:** hello@openclaw.ai

---

<div align="center">

**Made with ❤️ by OpenClaw Team**

[⬆️ 返回顶部](#openclaw-mobile-)

</div>
