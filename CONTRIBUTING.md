# Contributing to OpenClaw Mobile

欢迎贡献 OpenClaw Mobile 项目！

## 🤝 如何贡献

### 1. Fork 项目
点击右上角的 "Fork" 按钮

### 2. 克隆你的 Fork
```bash
git clone https://github.com/YOUR_USERNAME/openclaw-mobile.git
cd openclaw-mobile
```

### 3. 创建分支
```bash
git checkout -b feature/your-feature-name
```

### 4. 提交更改
```bash
git add .
git commit -m "Add some feature"
```

### 5. 推送到 Fork
```bash
git push origin feature/your-feature-name
```

### 6. 创建 Pull Request
在 GitHub 上点击 "New Pull Request"

## 📝 代码规范

### Kotlin 代码风格
- 使用 4 个空格缩进
- 遵循 Kotlin 官方代码风格指南
- 使用有意义的变量名
- 添加必要的注释

### 提交信息格式
```
feat: 添加新功能
fix: 修复 bug
docs: 更新文档
style: 代码格式调整
refactor: 重构代码
test: 添加测试
chore: 构建/工具配置
```

## 🐛 报告问题

### Bug 报告
请包含：
- 问题描述
- 复现步骤
- 预期行为
- 实际行为
- 环境信息（Android 版本、设备型号）

### 功能建议
请说明：
- 功能描述
- 使用场景
- 为什么需要这个功能

## 📖 开发指南

### 环境要求
- Android Studio Hedgehog (2024.1) 或更高版本
- JDK 17+
- Android SDK 34

### 构建项目
```bash
./gradlew assembleDebug
```

### 运行测试
```bash
./gradlew test
```

## 🔒 安全指南

发现安全漏洞请：
1. **不要** 公开报告
2. 发送邮件至：security@openclaw.ai
3. 等待确认和修复

## 💬 讨论

- GitHub Issues: 功能讨论和问题报告
- Discord: [加入社区](https://discord.gg/clawd)

## 📜 行为准则

- 尊重他人意见
- 建设性批评
- 保持开放心态
- 共同维护友好环境

## 🙏 致谢

感谢所有为 OpenClaw Mobile 做出贡献的开发者！

---

**最后更新：** 2026-03-14
