# OpenClaw Android PowerShell 构建脚本

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  OpenClaw Android 打包工具" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 检查 Java
Write-Host "[1/6] 检查 Java 环境..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1 | Select-String "version"
    Write-Host "[✓] $javaVersion" -ForegroundColor Green
} catch {
    Write-Host "[✗] 未找到 Java 环境" -ForegroundColor Red
    pause
    exit 1
}

# 检查 Gradle Wrapper
Write-Host "[2/6] 检查 Gradle Wrapper..." -ForegroundColor Yellow
if (Test-Path ".\gradlew.bat") {
    Write-Host "[✓] Gradle Wrapper 就绪" -ForegroundColor Green
} else {
    Write-Host "[✗] 未找到 gradlew.bat" -ForegroundColor Red
    pause
    exit 1
}

# 清理项目
Write-Host "[3/6] 清理项目..." -ForegroundColor Yellow
.\gradlew.bat clean
if ($LASTEXITCODE -eq 0) {
    Write-Host "[✓] 清理完成" -ForegroundColor Green
} else {
    Write-Host "[✗] 清理失败" -ForegroundColor Red
}

Write-Host ""

# 构建 Debug
Write-Host "[4/6] 构建 Debug 版本..." -ForegroundColor Yellow
.\gradlew.bat assembleDebug --no-daemon
if ($LASTEXITCODE -eq 0) {
    Write-Host "[✓] Debug 构建成功" -ForegroundColor Green
} else {
    Write-Host "[✗] Debug 构建失败" -ForegroundColor Red
    pause
    exit 1
}

Write-Host ""

# 构建 Release (如果有签名)
Write-Host "[5/6] 检查 Release 构建..." -ForegroundColor Yellow
if (Test-Path ".\keystore.properties") {
    Write-Host "发现签名配置，开始构建 Release..." -ForegroundColor Cyan
    .\gradlew.bat assembleRelease --no-daemon
    if ($LASTEXITCODE -eq 0) {
        Write-Host "[✓] Release 构建成功" -ForegroundColor Green
    } else {
        Write-Host "[⚠] Release 构建失败，请检查签名配置" -ForegroundColor Yellow
    }
} else {
    Write-Host "[⚠] 未找到 keystore.properties，跳过 Release 构建" -ForegroundColor Yellow
    Write-Host "提示：复制 keystore.properties.template 并填入实际值" -ForegroundColor Gray
}

Write-Host ""

# 复制到 H 盘
Write-Host "[6/6] 复制 APK 到 H 盘..." -ForegroundColor Yellow

$OutputDir = "H:\OpenClaw-APK"
if (-not (Test-Path $OutputDir)) {
    New-Item -ItemType Directory -Path $OutputDir | Out-Null
    Write-Host "[✓] 创建目录：$OutputDir" -ForegroundColor Green
}

# 复制 Debug APK
$DebugApk = "app\build\outputs\apk\debug\app-debug.apk"
if (Test-Path $DebugApk) {
    Copy-Item -Path $DebugApk -Destination "$OutputDir\OpenClaw-debug.apk" -Force
    Write-Host "[✓] Debug APK -> $OutputDir\OpenClaw-debug.apk" -ForegroundColor Green
} else {
    Write-Host "[✗] 未找到 Debug APK" -ForegroundColor Red
}

# 复制 Release APK
$ReleaseApk = "app\build\outputs\apk\release\app-release.apk"
if (Test-Path $ReleaseApk) {
    Copy-Item -Path $ReleaseApk -Destination "$OutputDir\OpenClaw-release.apk" -Force
    Write-Host "[✓] Release APK -> $OutputDir\OpenClaw-release.apk" -ForegroundColor Green
} else {
    Write-Host "[⚠] 未找到 Release APK (可能未配置签名)" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  打包完成！" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "输出目录：$OutputDir" -ForegroundColor Cyan
Write-Host ""
Write-Host "文件列表:" -ForegroundColor White
Get-ChildItem $OutputDir -Filter *.apk | Format-Table Name, @{Label="Size(MB)";Expression={[math]::Round($_.Length/1MB,2)}}, LastWriteTime -AutoSize

Write-Host ""
Write-Host "按任意键退出..." -ForegroundColor Gray
pause | Out-Null
