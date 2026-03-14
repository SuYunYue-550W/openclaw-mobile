# OpenClaw Android Build Script - Fixed Version

Write-Host "========================================"
Write-Host "  OpenClaw Android Build Tool"
Write-Host "========================================"
Write-Host ""

Set-Location $PSScriptRoot

# Set environment variables
$env:JAVA_HOME = "D:\jdk-17.0.9.8-hotspot"
$env:ANDROID_HOME = "D:\Android\SDK"
$env:Path = "$env:JAVA_HOME\bin;$env:ANDROID_HOME\platform-tools;$env:Path"

# Disable SSL certificate validation for Gradle
$env:GRADLE_OPTS = "-Djdk.internal.httpclient.disableHostnameVerification=true -Djdk.internal.httpclient.disableHttpUrlConnectionCertificateValidation=true"

Write-Host "Java Home: $env:JAVA_HOME"
Write-Host "Android Home: $env:ANDROID_HOME"
Write-Host ""

# Check Java
Write-Host "[1/5] Checking Java..."
try {
    & "$env:JAVA_HOME\bin\java.exe" -version 2>&1 | Out-Null
    Write-Host "[OK] Java found" -ForegroundColor Green
} catch {
    Write-Host "[ERROR] Java not found" -ForegroundColor Red
    Start-Sleep -Seconds 3
    exit 1
}

# Check Android SDK
Write-Host "[2/5] Checking Android SDK..."
if (Test-Path $env:ANDROID_HOME) {
    Write-Host "[OK] Android SDK found" -ForegroundColor Green
} else {
    Write-Host "[WARN] Android SDK not found at $env:ANDROID_HOME" -ForegroundColor Yellow
}

# Create local.properties
Write-Host "[3/5] Creating local.properties..."
@"
sdk.dir=$($env:ANDROID_HOME.Replace('\', '\\'))
ndk.dir=$($env:ANDROID_HOME.Replace('\', '\\'))\\ndk
"@ | Out-File -FilePath "local.properties" -Encoding ASCII
Write-Host "[OK] local.properties created" -ForegroundColor Green

# Clean
Write-Host "[4/5] Cleaning project..."
.\gradlew.bat clean --no-daemon --offline 2>$null
if ($LASTEXITCODE -eq 0) {
    Write-Host "[OK] Clean completed" -ForegroundColor Green
} else {
    Write-Host "[INFO] Clean skipped (offline mode)" -ForegroundColor Yellow
}

# Build Debug
Write-Host "[5/5] Building Debug APK..."
Write-Host "Note: First build may take 10-20 minutes" -ForegroundColor Cyan
.\gradlew.bat assembleDebug --no-daemon --stacktrace
if ($LASTEXITCODE -eq 0) {
    Write-Host "[OK] Debug build success!" -ForegroundColor Green
} else {
    Write-Host "[ERROR] Debug build failed" -ForegroundColor Red
    Write-Host ""
    Write-Host "Try these fixes:" -ForegroundColor Yellow
    Write-Host "1. Check network connection" -ForegroundColor White
    Write-Host "2. Run: .\gradlew.bat --stop" -ForegroundColor White
    Write-Host "3. Delete .gradle folder and try again" -ForegroundColor White
    Start-Sleep -Seconds 5
    exit 1
}

# Copy to H drive
Write-Host ""
Write-Host "========================================"
Write-Host "  Copying APK to H: drive"
Write-Host "========================================"
Write-Host ""

$OutputDir = "H:\OpenClaw-APK"
if (-not (Test-Path $OutputDir)) {
    New-Item -ItemType Directory -Path $OutputDir | Out-Null
    Write-Host "[OK] Created: $OutputDir" -ForegroundColor Green
}

$DebugApk = "app\build\outputs\apk\debug\app-debug.apk"
if (Test-Path $DebugApk) {
    Copy-Item -Path $DebugApk -Destination "$OutputDir\OpenClaw-debug.apk" -Force
    Write-Host "[OK] Debug APK copied" -ForegroundColor Green
    
    $size = (Get-Item "$OutputDir\OpenClaw-debug.apk").Length / 1MB
    Write-Host "    Size: $([math]::Round($size, 2)) MB" -ForegroundColor Gray
} else {
    Write-Host "[ERROR] APK not found" -ForegroundColor Red
}

Write-Host ""
Write-Host "========================================"
Write-Host "  Build Completed!"
Write-Host "========================================"
Write-Host ""
Write-Host "Output: H:\OpenClaw-APK\OpenClaw-debug.apk" -ForegroundColor Green
Write-Host ""
Start-Sleep -Seconds 3
