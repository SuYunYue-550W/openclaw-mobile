# 修复 Java SSL 证书问题

Write-Host "========================================"
Write-Host "  Fix Java SSL Certificates"
Write-Host "========================================"
Write-Host ""

$JAVA_HOME = "D:\jdk-17.0.9.8-hotspot"
$CACERTS = "$JAVA_HOME\lib\security\cacerts"

Write-Host "Java Home: $JAVA_HOME"
Write-Host "Certificates: $CACERTS"
Write-Host ""

# Check if running as admin
$isAdmin = ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)

if (-not $isAdmin) {
    Write-Host "[ERROR] Please run as Administrator" -ForegroundColor Red
    Write-Host "Right-click PowerShell and select 'Run as Administrator'" -ForegroundColor Yellow
    Start-Sleep -Seconds 3
    exit 1
}

# Backup existing cacerts
if (Test-Path $CACERTS) {
    Write-Host "[1/3] Backing up cacerts..."
    Copy-Item $CACERTS "$CACERTS.bak" -Force
    Write-Host "[OK] Backup created" -ForegroundColor Green
}

# Download and import certificates
Write-Host ""
Write-Host "[2/3] Importing certificates..."

# Import Aliyun certificate
Write-Host "Importing Aliyun certificate..."
$aliyunCert = "aliyun.crt"
try {
    # Download certificate
    Invoke-WebRequest -Uri "https://ssl-ca-cert.aliyuncs.com/root.crt" -OutFile $aliyunCert -UseBasicParsing
    
    # Import to keystore
    & "$JAVA_HOME\bin\keytool.exe" -importcert -noprompt -alias aliyuncs -file $aliyunCert -keystore $CACERTS -storepass changeit
    
    Write-Host "[OK] Aliyun certificate imported" -ForegroundColor Green
} catch {
    Write-Host "[WARN] Failed to import Aliyun certificate" -ForegroundColor Yellow
}

# Import Huawei certificate
Write-Host "Importing Huawei certificate..."
try {
    Invoke-WebRequest -Uri "https://repo.huaweicloud.com/ssl.crt" -OutFile "huawei.crt" -UseBasicParsing
    & "$JAVA_HOME\bin\keytool.exe" -importcert -noprompt -alias huaweicloud -file "huawei.crt" -keystore $CACERTS -storepass changeit
    Write-Host "[OK] Huawei certificate imported" -ForegroundColor Green
} catch {
    Write-Host "[WARN] Failed to import Huawei certificate" -ForegroundColor Yellow
}

# Clean up
Write-Host ""
Write-Host "[3/3] Cleaning up..."
Remove-Item -Path "*.crt" -Force -ErrorAction SilentlyContinue
Write-Host "[OK] Cleanup completed" -ForegroundColor Green

Write-Host ""
Write-Host "========================================"
Write-Host "  Certificate import completed!"
Write-Host "========================================"
Write-Host ""
Write-Host "Now try building again:" -ForegroundColor Cyan
Write-Host ".\build-fixed.ps1" -ForegroundColor White
Write-Host ""
Start-Sleep -Seconds 3
