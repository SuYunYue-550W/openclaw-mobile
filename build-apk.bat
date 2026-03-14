@echo off
REM OpenClaw Android 一键打包脚本

echo ========================================
echo   OpenClaw Android 打包工具
echo ========================================
echo.

REM 检查 Java 环境
echo [1/5] 检查 Java 环境...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未找到 Java 环境，请先安装 JDK 17+
    pause
    exit /b 1
)
echo [✓] Java 环境正常
echo.

REM 检查 Gradle
echo [2/5] 检查 Gradle...
if not exist "gradlew.bat" (
    echo [错误] 未找到 gradlew.bat，请确保在项目根目录运行
    pause
    exit /b 1
)
echo [✓] Gradle 就绪
echo.

REM 清理项目
echo [3/5] 清理项目...
call gradlew.bat clean
if %errorlevel% neq 0 (
    echo [错误] 清理失败
    pause
    exit /b 1
)
echo [✓] 清理完成
echo.

REM 构建 Debug 版本
echo [4/5] 构建 Debug 版本...
call gradlew.bat assembleDebug
if %errorlevel% neq 0 (
    echo [错误] Debug 构建失败
    pause
    exit /b 1
)
echo [✓] Debug 版本构建完成
echo.

REM 构建 Release 版本
echo [5/5] 构建 Release 版本...
echo 注意：Release 版本需要签名配置
echo 如果未配置签名，将跳过 Release 构建
echo.
set /p buildRelease="是否构建 Release 版本？(Y/N): "
if /i "%buildRelease%"=="Y" (
    if exist "keystore.properties" (
        call gradlew.bat assembleRelease
        if %errorlevel% neq 0 (
            echo [警告] Release 构建失败，请检查签名配置
        ) else (
            echo [✓] Release 版本构建完成
        )
    ) else (
        echo [跳过] 未找到 keystore.properties，跳过 Release 构建
        echo 提示：请参考 BUILD_GUIDE.md 配置签名
    )
)
echo.

REM 复制文件到 H 盘
echo ========================================
echo   复制 APK 到 H 盘
echo ========================================
echo.

if not exist "H:\OpenClaw-APK" (
    mkdir "H:\OpenClaw-APK"
    echo [✓] 创建目录 H:\OpenClaw-APK
)

REM 复制 Debug APK
if exist "app\build\outputs\apk\debug\app-debug.apk" (
    copy /Y "app\build\outputs\apk\debug\app-debug.apk" "H:\OpenClaw-APK\OpenClaw-debug.apk"
    echo [✓] Debug APK 已复制到 H:\OpenClaw-APK\OpenClaw-debug.apk
) else (
    echo [警告] 未找到 Debug APK
)

REM 复制 Release APK
if exist "app\build\outputs\apk\release\app-release.apk" (
    copy /Y "app\build\outputs\apk\release\app-release.apk" "H:\OpenClaw-APK\OpenClaw-release.apk"
    echo [✓] Release APK 已复制到 H:\OpenClaw-APK\OpenClaw-release.apk
)

echo.
echo ========================================
echo   打包完成！
echo ========================================
echo.
echo 输出目录：H:\OpenClaw-APK\
echo.
echo 文件列表:
dir H:\OpenClaw-APK\*.apk /B
echo.
echo 按任意键退出...
pause >nul
