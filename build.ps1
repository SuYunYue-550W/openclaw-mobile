# OpenClaw Android Build Script

Write-Host "========================================"
Write-Host "  OpenClaw Android Build Tool"
Write-Host "========================================"
Write-Host ""

Set-Location $PSScriptRoot

# Set JAVA_HOME
$env:JAVA_HOME = "D:\jdk-17.0.9.8-hotspot"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"

# Check Java
Write-Host "[1/4] Checking Java..."
try {
    & "$env:JAVA_HOME\bin\java.exe" -version 2>&1 | Out-Null
    Write-Host "[OK] Java found at $env:JAVA_HOME"
} catch {
    Write-Host "[ERROR] Java not found"
    Start-Sleep -Seconds 3
    exit 1
}

# Clean
Write-Host "[2/4] Cleaning project..."
.\gradlew.bat clean --no-daemon
if ($LASTEXITCODE -eq 0) {
    Write-Host "[OK] Clean completed"
} else {
    Write-Host "[WARN] Clean failed"
}

# Build Debug
Write-Host "[3/4] Building Debug APK..."
.\gradlew.bat assembleDebug --no-daemon
if ($LASTEXITCODE -eq 0) {
    Write-Host "[OK] Debug build success"
} else {
    Write-Host "[ERROR] Debug build failed"
    pause
    exit 1
}

# Copy to H drive
Write-Host "[4/4] Copying APK to H: drive..."

$OutputDir = "H:\OpenClaw-APK"
if (-not (Test-Path $OutputDir)) {
    New-Item -ItemType Directory -Path $OutputDir | Out-Null
}

$DebugApk = "app\build\outputs\apk\debug\app-debug.apk"
if (Test-Path $DebugApk) {
    Copy-Item -Path $DebugApk -Destination "$OutputDir\OpenClaw-debug.apk" -Force
    Write-Host "[OK] Debug APK copied to H:\OpenClaw-APK\"
}

$ReleaseApk = "app\build\outputs\apk\release\app-release.apk"
if (Test-Path $ReleaseApk) {
    Copy-Item -Path $ReleaseApk -Destination "$OutputDir\OpenClaw-release.apk" -Force
    Write-Host "[OK] Release APK copied to H:\OpenClaw-APK\"
}

Write-Host ""
Write-Host "========================================"
Write-Host "  Build Completed!"
Write-Host "========================================"
Write-Host ""
Write-Host "Output directory: H:\OpenClaw-APK\"
Write-Host ""
Get-ChildItem $OutputDir -Filter *.apk | Select-Object Name, @{Label="Size(MB)";Expression={[math]::Round($_.Length/1MB,2)}}

Write-Host ""
Write-Host "Build completed!"
Write-Host "Output: H:\OpenClaw-APK\"
Write-Host ""
Start-Sleep -Seconds 3
