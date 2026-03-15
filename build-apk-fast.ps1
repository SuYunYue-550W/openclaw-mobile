# OpenClaw Mobile Quick Build Script

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  OpenClaw Mobile - APK Build Tool" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Set-Location $PSScriptRoot

# Check Gradle
Write-Host "[1/4] Checking Gradle configuration..." -ForegroundColor Yellow
if (Test-Path ".\gradlew.bat") {
    Write-Host "[OK] Gradle Wrapper found" -ForegroundColor Green
} else {
    Write-Host "[ERROR] gradlew.bat not found" -ForegroundColor Red
    Write-Host "Please make sure you are in the correct project directory" -ForegroundColor Yellow
    pause
    exit 1
}

# Select build type
Write-Host ""
Write-Host "[2/4] Select build type:" -ForegroundColor Yellow
Write-Host "1. Debug version (for development)" -ForegroundColor White
Write-Host "2. Release version (for publishing)" -ForegroundColor White
$choice = Read-Host "Select (1/2)"

if ($choice -eq "1") {
    $buildType = "assembleDebug"
    $outputDir = "app\build\outputs\apk\debug"
    $apkName = "app-debug.apk"
} else {
    $buildType = "assembleRelease"
    $outputDir = "app\build\outputs\apk\release"
    $apkName = "app-release.apk"
}

# Clean build
Write-Host ""
Write-Host "[3/4] Cleaning old builds..." -ForegroundColor Yellow
.\gradlew.bat clean --no-daemon
if ($LASTEXITCODE -ne 0) {
    Write-Host "[WARN] Clean failed, continuing..." -ForegroundColor Yellow
}

# Start build
Write-Host ""
Write-Host "[4/4] Building $buildType ..." -ForegroundColor Yellow
Write-Host "Estimated time: 5-10 minutes" -ForegroundColor Cyan
Write-Host ""

.\gradlew.bat $buildType --no-daemon

# Check result
if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "  Build Successful!" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    Write-Host ""
    
    $apkPath = Join-Path $outputDir $apkName
    if (Test-Path $apkPath) {
        $size = (Get-Item $apkPath).Length / 1MB
        Write-Host "APK Location: $apkPath" -ForegroundColor Cyan
        Write-Host "APK Size: $([math]::Round($size, 2)) MB" -ForegroundColor Cyan
        Write-Host ""
        
        # Copy to output folder
        $outputFolder = "H:\OpenClaw-APK"
        if (-not (Test-Path $outputFolder)) {
            New-Item -ItemType Directory -Path $outputFolder | Out-Null
        }
        
        $destPath = Join-Path $outputFolder $apkName
        Copy-Item -Path $apkPath -Destination $destPath -Force
        Write-Host "Copied to: $destPath" -ForegroundColor Green
        Write-Host ""
        
        # Open folder
        Write-Host "Open APK folder?" -ForegroundColor Yellow
        $open = Read-Host "Y/N"
        if ($open -eq "Y" -or $open -eq "y") {
            explorer.exe /select, $apkPath
        }
    }
} else {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Red
    Write-Host "  Build Failed!" -ForegroundColor Red
    Write-Host "========================================" -ForegroundColor Red
    Write-Host ""
    Write-Host "Please check:" -ForegroundColor Yellow
    Write-Host "1. Gradle sync completed" -ForegroundColor White
    Write-Host "2. Network connection is OK" -ForegroundColor White
    Write-Host "3. Sufficient disk space" -ForegroundColor White
    Write-Host ""
}

Write-Host "Press any key to exit..." -ForegroundColor Gray
pause | Out-Null
