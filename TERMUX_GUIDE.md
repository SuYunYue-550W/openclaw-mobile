# Termux 集成指南

本指南介绍如何在 Android 手机上通过 Termux 实现类似桌面版 OpenClaw 的自动化功能。

## 📋 目录

1. [Termux 基础配置](#termux-基础配置)
2. [安装 OpenClaw CLI](#安装-openclaw-cli)
3. [配置本地模型](#配置本地模型)
4. [自动化脚本示例](#自动化脚本示例)
5. [与 OpenClaw Mobile 集成](#与-openclaw-mobile-集成)
6. [安全建议](#安全建议)

---

## Termux 基础配置

### 1. 安装 Termux

推荐从 F-Droid 下载最新版本：
- **F-Droid:** https://f-droid.org/packages/com.termux/
- **GitHub Releases:** https://github.com/termux/termux-app/releases

> ⚠️ 不要从 Google Play 下载，版本已过时！

### 2. 基础环境配置

```bash
# 更新软件源
pkg update && pkg upgrade

# 安装必要工具
pkg install python git cmake clang wget curl

# 安装 Python 虚拟环境
pkg install python-virtualenv

# 配置 pip 镜像（中国大陆用户）
pip config set global.index-url https://pypi.tuna.tsinghua.edu.cn/simple
```

### 3. 存储权限

```bash
# 授予存储访问权限
termux-setup-storage

# 创建项目目录
mkdir -p ~/openclaw
cd ~/openclaw
```

---

## 安装 OpenClaw CLI

### 方式一：通过 pip 安装

```bash
# 安装 openclaw-cli
pip install openclaw-cli

# 验证安装
openclaw --version
```

### 方式二：从源码安装

```bash
# 克隆仓库
git clone https://github.com/openclaw/openclaw.git
cd openclaw

# 安装依赖
pip install -e .

# 验证安装
openclaw --version
```

### 配置 OpenClaw

```bash
# 运行配置向导
openclaw configure

# 或手动配置
mkdir -p ~/.openclaw
cat > ~/.openclaw/config.json << EOF
{
  "models": {
    "default": "qwen-plus",
    "providers": {
      "aliyun": {
        "apiKey": "YOUR_API_KEY_HERE"
      }
    }
  }
}
EOF
```

---

## 配置本地模型

### 1. 下载模型

```bash
# 创建模型目录
mkdir -p ~/models

# 使用 huggingface-cli 下载（需要安装）
pip install huggingface_hub
huggingface-cli download Qwen/Qwen2.5-1.5B-Instruct --local-dir ~/models/qwen2.5-1.5b

# 或使用 ModelScope（中国大陆推荐）
pip install modelscope
modelscope download qwen/Qwen2.5-1.5B-Instruct --local_dir ~/models/qwen2.5-1.5b
```

### 2. 安装 llama.cpp

```bash
# 克隆 llama.cpp
git clone https://github.com/ggerganov/llama.cpp
cd llama.cpp

# 编译
make -j4

# 验证安装
./main --help
```

### 3. 转换模型为 GGUF 格式

```bash
# 进入 llama.cpp 目录
cd ~/llama.cpp

# 转换模型
python convert-hf-to-gguf.py ~/models/qwen2.5-1.5b \
  --outfile ~/models/qwen2.5-1.5b.gguf \
  --outtype q4_k_m
```

### 4. 运行本地模型

```bash
# 运行推理
./main -m ~/models/qwen2.5-1.5b.gguf \
  -p "你好，请介绍一下自己" \
  -n 512 \
  --temp 0.7 \
  --repeat_penalty 1.1
```

---

## 自动化脚本示例

### 示例 1: 自动回复消息

```bash
#!/data/data/com.termux/files/usr/bin/bash
# save as: ~/openclaw/auto_reply.sh

MESSAGE="$1"

if [ -z "$MESSAGE" ]; then
    echo "用法：auto_reply.sh <消息内容>"
    exit 1
fi

# 调用 OpenClaw API
RESPONSE=$(openclaw agent --message "$MESSAGE" --deliver)

# 输出回复
echo "$RESPONSE"

# 可选：通过 Termux:API 发送通知
notify-send "OpenClaw 回复" "$RESPONSE"
```

### 示例 2: 定时任务

```bash
#!/data/data/com.termux/files/usr/bin/bash
# save as: ~/openclaw/daily_briefing.sh

# 获取天气
WEATHER=$(curl -s "https://wttr.in/Beijing?format=3")

# 获取新闻摘要
NEWS=$(openclaw agent --message "总结今天的科技新闻" --deliver)

# 生成日报
cat << EOF | termux-notification --title "每日简报"
$WEATHER

$NEWS
EOF
```

### 示例 3: 语音助手

```bash
#!/data/data/com.termux/files/usr/bin/bash
# save as: ~/openclaw/voice_assistant.sh

# 录音
echo "请说话..."
termux-microphone-record -l 5 -f ~/voice_input.wav

# 语音转文字（需要配置）
TEXT=$(python ~/speech_to_text.py ~/voice_input.wav)

# 调用 OpenClaw
RESPONSE=$(openclaw agent --message "$TEXT" --deliver)

# 文字转语音
termux-tts-speak "$RESPONSE"
```

### 设置定时任务

```bash
# 安装 cronie
pkg install cronie

# 编辑 crontab
crontab -e

# 添加每日早上 8 点的简报
0 8 * * * /data/data/com.termux/files/home/openclaw/daily_briefing.sh
```

---

## 与 OpenClaw Mobile 集成

### 共享模型文件

```bash
# 在 Termux 中创建符号链接
ln -s /sdcard/Android/data/com.openclaw.mobile/files/models ~/mobile_models

# 现在两个应用可以共享模型文件
```

### 通过 Intent 调用

```bash
# 从 Termux 启动 OpenClaw Mobile
am start -n com.openclaw.mobile/.ui.MainActivity \
  --es "message" "你好"
```

### 共享 API 配置

```bash
# 导出 API 配置
cp ~/.openclaw/config.json /sdcard/Android/data/com.openclaw.mobile/files/

# 在 OpenClaw Mobile 中导入配置
```

---

## 安全建议

### 1. Termux 安全配置

```bash
# 设置 Termux 密码
passwd

# 禁用不必要的权限
pm revoke com.termux android.permission.READ_EXTERNAL_STORAGE
```

### 2. API Key 保护

```bash
# 使用加密存储
termux-setup-storage
chmod 600 ~/.openclaw/config.json

# 或使用环境变量
export OPENCLAW_API_KEY="your_key_here"
```

### 3. 网络隔离

```bash
# 只允许本地访问
openclaw gateway --bind 127.0.0.1

# 或使用 Tailscale 进行安全远程访问
pkg install tailscale
tailscale up
```

### 4. 定期备份

```bash
#!/data/data/com.termux/files/usr/bin/bash
# backup.sh

BACKUP_DIR=~/backups/$(date +%Y%m%d)
mkdir -p $BACKUP_DIR

# 备份配置
cp -r ~/.openclaw $BACKUP_DIR/

# 备份模型列表
ls ~/models > $BACKUP_DIR/models.txt

# 压缩备份
tar -czf $BACKUP_DIR.tar.gz $BACKUP_DIR

echo "备份完成：$BACKUP_DIR.tar.gz"
```

---

## 常见问题

### Q: Termux 无法访问外部存储？
```bash
# 授予权限
appops set com.termux ACCESS_MEDIA_LOCATION allow

# 重新运行
termux-setup-storage
```

### Q: 模型下载太慢？
```bash
# 使用国内镜像
export HF_ENDPOINT=https://hf-mirror.com
pip install modelscope  # 使用 ModelScope
```

### Q: 运行模型时内存不足？
```bash
# 使用更小的量化版本
./main -m model.gguf -n 256 --temp 0.7

# 或关闭其他应用释放内存
```

### Q: 如何让脚本开机自启？
```bash
# 使用 termux-boot
pkg install termux-boot
mkdir -p ~/.termux/boot/
cp ~/openclaw/startup.sh ~/.termux/boot/
```

---

## 进阶：构建完整的自动化系统

### 系统架构

```
┌─────────────────┐     ┌──────────────────┐     ┌─────────────────┐
│  OpenClaw       │────▶│   Termux         │────▶│  本地模型       │
│  Mobile (UI)    │     │   (自动化)       │     │  (推理)         │
└─────────────────┘     └──────────────────┘     └─────────────────┘
         │                       │                        │
         ▼                       ▼                        ▼
┌─────────────────┐     ┌──────────────────┐     ┌─────────────────┐
│  云端 API        │     │  定时任务        │     │  文件处理       │
│  (备用)         │     │  (cron)          │     │  (Python)       │
└─────────────────┘     └──────────────────┘     └─────────────────┘
```

### 示例项目结构

```
~/openclaw/
├── auto_reply.sh          # 自动回复
├── daily_briefing.sh      # 每日简报
├── voice_assistant.sh     # 语音助手
├── backup.sh              # 备份脚本
├── speech_to_text.py      # 语音识别
├── config.json            # 配置文件
└── logs/                  # 日志目录
```

---

## 参考资源

- [Termux 官方文档](https://wiki.termux.com/)
- [OpenClaw 文档](https://docs.openclaw.ai/)
- [llama.cpp GitHub](https://github.com/ggerganov/llama.cpp)
- [ModelScope](https://modelscope.cn/)

---

<div align="center">

**祝你使用愉快！🦞**

遇到问题？[提交 Issue](https://github.com/openclaw/openclaw-mobile/issues)

</div>
