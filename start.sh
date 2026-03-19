#!/bin/bash
# ============================================================
#  非遗技艺师徒档案传承管理系统 — 一键启动（macOS）
# ============================================================
set -e

BASE_DIR="$(cd "$(dirname "$0")" && pwd)"
LOG_DIR="$BASE_DIR/.logs"
mkdir -p "$LOG_DIR"

# ── 颜色 ──
R='\033[0;31m'; G='\033[0;32m'; Y='\033[1;33m'; C='\033[0;36m'; B='\033[1m'; N='\033[0m'

# ── 清理旧进程 ──
cleanup() {
    echo -e "\n${Y}▶ 正在停止所有服务...${N}"
    for f in "$LOG_DIR"/*.pid; do
        [ -f "$f" ] && kill "$(cat "$f")" 2>/dev/null && rm "$f"
    done
    lsof -ti:8100 | xargs kill -9 2>/dev/null || true
    echo -e "${G}✅ 所有服务已停止${N}"
    exit 0
}
trap cleanup SIGINT SIGTERM

echo ""
echo -e "${C}╔══════════════════════════════════════════════════════╗${N}"
echo -e "${C}║  ${B}非遗技艺师徒档案传承管理系统${N}${C}                       ║${N}"
echo -e "${C}║  一键启动脚本 (macOS)          Ctrl+C 停止所有服务   ║${N}"
echo -e "${C}╚══════════════════════════════════════════════════════╝${N}"
echo ""

# ═══════════════════════════════════════════
# 1. 数据库导入
# ═══════════════════════════════════════════
DB_NAME="heritage_archive"
DB_USER="root"
DB_PASS="ab123168"
MYSQL="mysql -u${DB_USER} -p${DB_PASS}"

echo -e "${Y}▶ [1/3] 检查数据库...${N}"

# 检查 MySQL 是否可连接
if ! $MYSQL -e "SELECT 1" &>/dev/null; then
    echo -e "  ${R}✘ 无法连接 MySQL，请确保 MySQL 正在运行${N}"
    echo -e "  ${Y}尝试启动: brew services start mysql${N}"
    exit 1
fi
echo -e "  ${G}✓ MySQL 连接成功${N}"

# 检查数据库是否已存在并有数据
DB_EXISTS=$($MYSQL -N -e "SELECT COUNT(*) FROM information_schema.SCHEMATA WHERE SCHEMA_NAME='${DB_NAME}'" 2>/dev/null)
TABLE_COUNT=$($MYSQL -N -e "SELECT COUNT(*) FROM information_schema.TABLES WHERE TABLE_SCHEMA='${DB_NAME}'" 2>/dev/null || echo "0")

if [ "$DB_EXISTS" -gt 0 ] && [ "$TABLE_COUNT" -gt 5 ]; then
    echo -e "  ${G}✓ 数据库 ${DB_NAME} 已存在 (${TABLE_COUNT} 张表)，跳过导入${N}"
else
    echo -e "  ${Y}↻ 正在创建并导入数据库...${N}"
    $MYSQL -e "CREATE DATABASE IF NOT EXISTS \`${DB_NAME}\` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;" 2>/dev/null
    $MYSQL "$DB_NAME" < "$BASE_DIR/sql/init.sql" 2>/dev/null
    echo -e "  ${G}✓ 表结构导入完成${N}"
    $MYSQL "$DB_NAME" < "$BASE_DIR/sql/data.sql" 2>/dev/null
    echo -e "  ${G}✓ 初始数据导入完成${N}"
fi

# 兼容旧库：补齐 teaching_task.apprentice_id 字段
TASK_APPRENTICE_COL=$($MYSQL -N -e "SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA='${DB_NAME}' AND TABLE_NAME='teaching_task' AND COLUMN_NAME='apprentice_id'" 2>/dev/null || echo "0")
if [ "$TASK_APPRENTICE_COL" -eq 0 ]; then
    echo -e "  ${Y}↻ 检测到旧版 teaching_task，正在补齐 apprentice_id 字段...${N}"
    $MYSQL "$DB_NAME" -e "ALTER TABLE teaching_task ADD COLUMN apprentice_id BIGINT NULL COMMENT '指定徒弟ID，NULL表示面向全部有效徒弟' AFTER master_id;"
    $MYSQL "$DB_NAME" -e "ALTER TABLE teaching_task ADD INDEX idx_apprentice_id (apprentice_id);"
    echo -e "  ${G}✓ teaching_task.apprentice_id 补齐完成${N}"
fi
echo ""

# ═══════════════════════════════════════════
# 2. 启动后端 (Spring Boot)
# ═══════════════════════════════════════════
echo -e "${Y}▶ [2/3] 启动后端服务 (Spring Boot :8100)...${N}"

# Kill any existing process on port 8100
lsof -ti:8100 | xargs kill -9 2>/dev/null || true

cd "$BASE_DIR/backend"

# Check if jar exists, if not compile
JAR_FILE=$(find target -name "*.jar" -not -name "*-sources.jar" 2>/dev/null | head -1)
NEED_BUILD=0
if [ -z "$JAR_FILE" ]; then
    NEED_BUILD=1
else
    LATEST_SRC_TS=$(find src pom.xml -type f -exec stat -f %m {} \; | sort -nr | head -1)
    JAR_TS=$(stat -f %m "$JAR_FILE")
    if [ "$LATEST_SRC_TS" -gt "$JAR_TS" ]; then
        NEED_BUILD=1
    fi
fi

if [ "$NEED_BUILD" -eq 1 ]; then
    echo -e "  ${Y}↻ 检测到后端需要重新打包，正在执行 mvn package...${N}"
    mvn package -DskipTests -q 2>"$LOG_DIR/maven-build.log"
    JAR_FILE=$(find target -name "*.jar" -not -name "*-sources.jar" 2>/dev/null | head -1)
fi

if [ -z "$JAR_FILE" ]; then
    echo -e "  ${R}✘ 编译失败，请检查 $LOG_DIR/maven-build.log${N}"
    exit 1
fi

nohup java -jar "$JAR_FILE" > "$LOG_DIR/backend.log" 2>&1 &
echo $! > "$LOG_DIR/backend.pid"
echo -e "  ${G}✓ 后端启动中 (PID: $(cat $LOG_DIR/backend.pid))${N}"

# Wait for backend to be ready
echo -ne "  等待后端就绪"
for i in $(seq 1 30); do
    if curl -s -o /dev/null http://localhost:8100/api/auth/login -X POST -H "Content-Type: application/json" -d '{}' 2>/dev/null; then
        echo -e "\r  ${G}✓ 后端已就绪 (http://localhost:8100)        ${N}"
        break
    fi
    echo -n "."
    sleep 2
done
echo ""

echo -e "${Y}▶ [3/4] 启动管理端前端 (Vite)...${N}"
cd "$BASE_DIR/admin"

# Install deps if needed
if [ ! -d "node_modules" ]; then
    echo -e "  ${Y}↻ 安装前端依赖 (pnpm install)...${N}"
    pnpm install --silent 2>"$LOG_DIR/pnpm-install.log"
fi

nohup pnpm dev > "$LOG_DIR/admin.log" 2>&1 &
echo $! > "$LOG_DIR/admin.pid"
echo -e "  ${G}✓ 管理端启动中 (PID: $(cat $LOG_DIR/admin.pid))${N}"

# Wait for Vite
sleep 3
ADMIN_URL=$(grep -oE 'http://localhost:[0-9]+' "$LOG_DIR/admin.log" 2>/dev/null | head -1)
ADMIN_URL=${ADMIN_URL:-"http://localhost:5173"}
echo -e "  ${G}✓ 管理端地址: ${B}${ADMIN_URL}${N}"
echo ""

# ═══════════════════════════════════════════
# 4. 启动用户端前端 (Frontend Vite)
# ═══════════════════════════════════════════
echo -e "${Y}▶ [4/4] 启动用户端前端 (Vite :5174)...${N}"
cd "$BASE_DIR/frontend"

# Install deps if needed
if [ ! -d "node_modules" ]; then
    echo -e "  ${Y}↻ 安装前端依赖...${N}"
    if command -v pnpm &>/dev/null; then
        pnpm install --silent 2>"$LOG_DIR/frontend-install.log"
    else
        npm install --silent 2>"$LOG_DIR/frontend-install.log"
    fi
fi

nohup npm run dev > "$LOG_DIR/frontend.log" 2>&1 &
echo $! > "$LOG_DIR/frontend.pid"
echo -e "  ${G}✓ 用户端启动中 (PID: $(cat $LOG_DIR/frontend.pid))${N}"

sleep 3
FRONTEND_URL=$(grep -oE 'http://localhost:[0-9]+' "$LOG_DIR/frontend.log" 2>/dev/null | head -1)
FRONTEND_URL=${FRONTEND_URL:-"http://localhost:5174"}
echo -e "  ${G}✓ 用户端地址: ${B}${FRONTEND_URL}${N}"
echo ""

# ═══════════════════════════════════════════
# 5. 账号信息
# ═══════════════════════════════════════════
echo -e "${B}══════════════════════════════════════════════════════${N}"
echo -e "${B}🔑 系统账号信息${N}"
echo -e "${B}══════════════════════════════════════════════════════${N}"
echo -e "  ┌──────────────┬──────────────┬──────────┬──────────────┐"
echo -e "  │ ${B}角色${N}         │ ${B}用户名${N}       │ ${B}密码${N}     │ ${B}姓名${N}         │"
echo -e "  ├──────────────┼──────────────┼──────────┼──────────────┤"
echo -e "  │ ${R}管理员${N}       │ admin        │ admin123 │ 系统管理员   │"
echo -e "  ├──────────────┼──────────────┼──────────┼──────────────┤"
echo -e "  │ ${Y}传承大师${N}     │ master1      │ 123456   │ 张文华       │"
echo -e "  │              │ master2      │ 123456   │ 李秀英       │"
echo -e "  │              │ master3      │ 123456   │ 王德明       │"
echo -e "  ├──────────────┼──────────────┼──────────┼──────────────┤"
echo -e "  │ ${G}学徒${N}         │ apprentice1  │ 123456   │ 陈小雨       │"
echo -e "  │              │ apprentice2  │ 123456   │ 刘学文       │"
echo -e "  │              │ apprentice3  │ 123456   │ 赵雅琴       │"
echo -e "  │              │ apprentice4  │ 123456   │ 孙明轩       │"
echo -e "  │              │ apprentice5  │ 123456   │ 周慧敏       │"
echo -e "  └──────────────┴──────────────┴──────────┴──────────────┘"
echo ""
echo -e "  ${C}后端地址:${N} http://localhost:8100"
echo -e "  ${C}管理端地址:${N} ${ADMIN_URL}"
echo -e "  ${C}用户端地址:${N} ${FRONTEND_URL}"
echo ""

# ═══════════════════════════════════════════
# 6. 实时日志
# ═══════════════════════════════════════════
echo -e "${C}══════════════════════════════════════════════════════${N}"
echo -e "${B}📺 实时日志 (Ctrl+C 停止所有服务并退出)${N}"
echo -e "${C}══════════════════════════════════════════════════════${N}"
tail -f "$LOG_DIR/backend.log" "$LOG_DIR/admin.log" "$LOG_DIR/frontend.log" 2>/dev/null
