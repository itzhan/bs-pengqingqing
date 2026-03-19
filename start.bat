@echo off
chcp 65001 >nul 2>&1
title 非遗技艺师徒档案传承管理系统
setlocal enabledelayedexpansion

echo.
echo ╔══════════════════════════════════════════════════════╗
echo ║  非遗技艺师徒档案传承管理系统                       ║
echo ║  一键启动脚本 (Windows)                             ║
echo ╚══════════════════════════════════════════════════════╝
echo.

set "BASE_DIR=%~dp0"
set "LOG_DIR=%BASE_DIR%.logs"
if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"

:: ═══════════════════════════════════════════
:: 1. 启动后端 (Spring Boot)
:: ═══════════════════════════════════════════
echo [1/3] 启动后端服务 (Spring Boot :8100)...

cd /d "%BASE_DIR%backend"

:: 查找 jar 文件
set "JAR_FILE="
for /r "target" %%f in (*.jar) do (
    echo %%f | findstr /i "sources" >nul || set "JAR_FILE=%%f"
)

if "!JAR_FILE!"=="" (
    echo   * 未找到 jar 包，正在编译 (mvn package)...
    call mvn package -DskipTests -q 2>"%LOG_DIR%\maven-build.log"
    for /r "target" %%f in (*.jar) do (
        echo %%f | findstr /i "sources" >nul || set "JAR_FILE=%%f"
    )
)

if "!JAR_FILE!"=="" (
    echo   X 编译失败，请检查 %LOG_DIR%\maven-build.log
    pause
    exit /b 1
)

echo   启动: !JAR_FILE!
start "Heritage-Backend" /min cmd /c "java -jar "!JAR_FILE!" > "%LOG_DIR%\backend.log" 2>&1"
echo   √ 后端启动中...

:: 等待后端就绪
echo   等待后端就绪...
set /a "retry=0"
:wait_backend
set /a "retry+=1"
if !retry! gtr 30 (
    echo   ! 后端启动超时，请检查 %LOG_DIR%\backend.log
    goto start_admin
)
timeout /t 2 /nobreak >nul
curl -s -o nul http://localhost:8100/api/auth/login -X POST -H "Content-Type: application/json" -d "{}" 2>nul
if errorlevel 1 goto wait_backend
echo   √ 后端已就绪 (http://localhost:8100)
echo.

:: ═══════════════════════════════════════════
:: 2. 启动管理端前端 (Admin Vite)
:: ═══════════════════════════════════════════
:start_admin
echo [2/3] 启动管理端前端 (Vite :5173)...

cd /d "%BASE_DIR%admin"

if not exist "node_modules" (
    echo   * 安装前端依赖 (pnpm install)...
    call pnpm install --silent 2>"%LOG_DIR%\pnpm-install.log"
)

start "Heritage-Admin" /min cmd /c "pnpm dev > "%LOG_DIR%\admin.log" 2>&1"
echo   √ 管理端启动中...
timeout /t 3 /nobreak >nul
echo   √ 管理端地址: http://localhost:5173
echo.

:: ═══════════════════════════════════════════
:: 3. 启动用户端前端 (Frontend Vite)
:: ═══════════════════════════════════════════
echo [3/3] 启动用户端前端 (Vite :5174)...

cd /d "%BASE_DIR%frontend"

if not exist "node_modules" (
    echo   * 安装前端依赖...
    where pnpm >nul 2>&1
    if !errorlevel! equ 0 (
        call pnpm install --silent 2>"%LOG_DIR%\frontend-install.log"
    ) else (
        call npm install --silent 2>"%LOG_DIR%\frontend-install.log"
    )
)

start "Heritage-Frontend" /min cmd /c "npm run dev > "%LOG_DIR%\frontend.log" 2>&1"
echo   √ 用户端启动中...
timeout /t 3 /nobreak >nul
echo   √ 用户端地址: http://localhost:5174
echo.

:: ═══════════════════════════════════════════
:: 4. 账号信息
:: ═══════════════════════════════════════════
echo ══════════════════════════════════════════════════════
echo   系统账号信息
echo ══════════════════════════════════════════════════════
echo   ┌──────────────┬──────────────┬──────────┬──────────────┐
echo   │ 角色         │ 用户名       │ 密码     │ 姓名         │
echo   ├──────────────┼──────────────┼──────────┼──────────────┤
echo   │ 管理员       │ admin        │ admin123 │ 系统管理员   │
echo   ├──────────────┼──────────────┼──────────┼──────────────┤
echo   │ 传承大师     │ master1      │ 123456   │ 张文华       │
echo   │              │ master2      │ 123456   │ 李秀英       │
echo   │              │ master3      │ 123456   │ 王德明       │
echo   ├──────────────┼──────────────┼──────────┼──────────────┤
echo   │ 学徒         │ apprentice1  │ 123456   │ 陈小雨       │
echo   │              │ apprentice2  │ 123456   │ 刘学文       │
echo   │              │ apprentice3  │ 123456   │ 赵雅琴       │
echo   │              │ apprentice4  │ 123456   │ 孙明轩       │
echo   │              │ apprentice5  │ 123456   │ 周慧敏       │
echo   └──────────────┴──────────────┴──────────┴──────────────┘
echo.
echo   后端地址:   http://localhost:8100
echo   管理端地址: http://localhost:5173
echo   用户端地址: http://localhost:5174
echo.
echo ══════════════════════════════════════════════════════
echo   按任意键查看实时日志，Ctrl+C 关闭此窗口
echo   (后端、管理端和用户端在各自的最小化窗口中运行)
echo ══════════════════════════════════════════════════════
pause >nul

:: 显示日志
type "%LOG_DIR%\backend.log"
echo.
echo --- 管理端日志 ---
type "%LOG_DIR%\admin.log"
echo.
echo --- 用户端日志 ---
type "%LOG_DIR%\frontend.log"
pause

