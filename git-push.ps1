# Git 推送脚本
# 使用方法：.\git-push.ps1

Write-Host "🦞 OpenClaw Mobile - Git 推送脚本" -ForegroundColor Cyan
Write-Host "=" * 50

# 进入项目目录
Set-Location "C:\Projects\OpenClaw-Android"

# 检查 Git 是否可用
try {
    $gitVersion = git --version 2>&1
    Write-Host "✓ Git 版本：$gitVersion" -ForegroundColor Green
} catch {
    Write-Host "✗ 错误：未找到 Git，请先安装 Git" -ForegroundColor Red
    Write-Host "下载地址：https://git-scm.com/download/win" -ForegroundColor Yellow
    exit 1
}

# 检查是否已初始化 Git 仓库
if (-not (Test-Path ".git")) {
    Write-Host "⚠ Git 仓库未初始化，正在初始化..." -ForegroundColor Yellow
    git init
}

# 检查远程仓库配置
$remoteUrl = git config --get remote.origin.url 2>$null
if (-not $remoteUrl) {
    Write-Host "⚠ 未配置远程仓库" -ForegroundColor Yellow
    Write-Host "请输入 GitHub 仓库地址 (例如：https://github.com/your-username/openclaw-mobile):"
    $repoUrl = Read-Host
    git remote add origin $repoUrl
} else {
    Write-Host "✓ 远程仓库：$remoteUrl" -ForegroundColor Green
}

# 添加文件
Write-Host "`n📦 添加文件..." -ForegroundColor Cyan
git add .
if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ git add 失败" -ForegroundColor Red
    exit 1
}
Write-Host "✓ 文件已添加" -ForegroundColor Green

# 查看状态
Write-Host "`n📋 Git 状态:" -ForegroundColor Cyan
git status --short

# 提交
Write-Host "`n💾 提交更改..." -ForegroundColor Cyan
$commitMessage = "Initial commit: OpenClaw Mobile v1.0"
git commit -m $commitMessage
if ($LASTEXITCODE -ne 0) {
    Write-Host "⚠ 可能没有需要提交的更改" -ForegroundColor Yellow
}

# 推送
Write-Host "`n🚀 推送到 GitHub..." -ForegroundColor Cyan
Write-Host "提示：如果是第一次推送，可能需要输入 GitHub 用户名和密码（或个人访问令牌）" -ForegroundColor Yellow

git push -u origin main
if ($LASTEXITCODE -ne 0) {
    Write-Host "`n✗ 推送失败" -ForegroundColor Red
    Write-Host "`n可能的解决方案:" -ForegroundColor Yellow
    Write-Host "1. 如果是认证问题，请使用个人访问令牌（不是密码）" -ForegroundColor White
    Write-Host "   创建令牌：https://github.com/settings/tokens" -ForegroundColor White
    Write-Host "2. 如果仓库不存在，请先在 GitHub 创建空仓库" -ForegroundColor White
    Write-Host "3. 或者使用 GitHub Desktop 工具" -ForegroundColor White
    exit 1
}

Write-Host "`n" -NoNewline
Write-Host "🎉 成功！" -ForegroundColor Green
Write-Host "你的代码已推送到 GitHub" -ForegroundColor White
Write-Host "`n查看仓库：" -NoNewline
Write-Host $remoteUrl -ForegroundColor Cyan
