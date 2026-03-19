#!/bin/bash
# ============================================================
# 非遗技艺师徒档案传承管理系统 — 全接口测试脚本
# ============================================================
set -e
BASE="http://localhost:8100/api"
PASS=0; FAIL=0; TOTAL=0

G='\033[0;32m'; R='\033[0;31m'; Y='\033[1;33m'; N='\033[0m'

api() {
  local method="$1" path="$2" token="$3" body="$4" label="$5"
  TOTAL=$((TOTAL+1))
  local args=(-s -o /tmp/api_resp.json -w "%{http_code}" -X "$method" "${BASE}${path}" -H "Content-Type: application/json")
  [ -n "$token" ] && args+=(-H "Authorization: Bearer $token")
  [ -n "$body" ] && args+=(-d "$body")
  local code
  code=$(curl "${args[@]}" 2>/dev/null)
  # Check response code field too
  local ok
  ok=$(cat /tmp/api_resp.json 2>/dev/null | python3 -c "import sys,json;d=json.load(sys.stdin);print(d.get('code',999))" 2>/dev/null || echo "999")
  if [[ "$code" == "200" && ("$ok" == "200" || "$ok" == "0") ]]; then
    PASS=$((PASS+1))
    echo -e "  ${G}✓${N} [${code}] ${label}"
  else
    FAIL=$((FAIL+1))
    echo -e "  ${R}✘${N} [${code}|code=${ok}] ${label}"
    cat /tmp/api_resp.json 2>/dev/null | python3 -c "import sys,json;d=json.load(sys.stdin);print('    →',d.get('msg',d.get('message','')))" 2>/dev/null || true
  fi
}

login() {
  local user="$1" pass="$2"
  local resp
  resp=$(curl -s -X POST "${BASE}/auth/login" -H "Content-Type: application/json" -d "{\"username\":\"${user}\",\"password\":\"${pass}\"}" 2>/dev/null)
  echo "$resp" | python3 -c "import sys,json;d=json.load(sys.stdin);print(d['data']['token'])" 2>/dev/null
}

echo ""
echo -e "${Y}╔══════════════════════════════════════════════════╗${N}"
echo -e "${Y}║  非遗系统 API 全接口测试                        ║${N}"
echo -e "${Y}╚══════════════════════════════════════════════════╝${N}"
echo ""

# ── 登录获取 Token ──
echo -e "${Y}▶ 登录 & 认证${N}"
ADMIN_TOKEN=$(login admin admin123)
MASTER_TOKEN=$(login master1 123456)
MASTER2_TOKEN=$(login master2 123456)
MASTER3_TOKEN=$(login master3 123456)
APPRENTICE_TOKEN=$(login apprentice1 123456)
APPRENTICE3_TOKEN=$(login apprentice3 123456)
APPRENTICE5_TOKEN=$(login apprentice5 123456)

[ -z "$ADMIN_TOKEN" ] && echo -e "${R}✘ Admin 登录失败，终止测试${N}" && exit 1
[ -z "$MASTER_TOKEN" ] && echo -e "${R}✘ Master1 登录失败${N}" && exit 1
[ -z "$APPRENTICE_TOKEN" ] && echo -e "${R}✘ Apprentice1 登录失败${N}" && exit 1

api POST "/auth/login" "" '{"username":"admin","password":"admin123"}' "管理员登录"
api POST "/auth/login" "" '{"username":"master1","password":"123456"}' "师傅登录"
api POST "/auth/login" "" '{"username":"apprentice1","password":"123456"}' "徒弟登录"
api GET  "/auth/info" "$ADMIN_TOKEN" "" "获取当前用户信息(admin)"
api GET  "/auth/info" "$MASTER_TOKEN" "" "获取当前用户信息(master)"
api GET  "/auth/info" "$APPRENTICE_TOKEN" "" "获取当前用户信息(apprentice)"

# ── 用户管理 ──
echo -e "\n${Y}▶ 用户管理${N}"
api GET  "/users/?page=1&size=10" "$ADMIN_TOKEN" "" "用户列表(管理员)"
api GET  "/users/2" "$ADMIN_TOKEN" "" "用户详情"
api PUT  "/users/profile" "$APPRENTICE_TOKEN" '{"realName":"陈小雨","phone":"13700001001","email":"test@test.com"}' "更新个人资料"

# ── 技艺类别 ──
echo -e "\n${Y}▶ 技艺类别${N}"
api GET  "/skill-categories/" "$ADMIN_TOKEN" "" "类别列表"
api GET  "/skill-categories/tree" "$ADMIN_TOKEN" "" "类别树"

# ── 非遗项目 ──
echo -e "\n${Y}▶ 非遗项目${N}"
api GET  "/heritage-projects/?page=1&size=10" "$ADMIN_TOKEN" "" "项目列表(分页)"
api GET  "/heritage-projects/?page=1&size=5&level=%E5%9B%BD%E5%AE%B6%E7%BA%A7" "$ADMIN_TOKEN" "" "项目列表(按级别筛选)"
api GET  "/heritage-projects/?page=1&size=5&keyword=%E8%93%9D%E5%8D%B0" "$ADMIN_TOKEN" "" "项目列表(关键词搜索)"
api GET  "/heritage-projects/all" "$ADMIN_TOKEN" "" "全部项目列表"
api GET  "/heritage-projects/1" "$ADMIN_TOKEN" "" "项目详情"

# ── 师傅档案 ──
echo -e "\n${Y}▶ 师傅档案${N}"
api GET  "/master-profiles/?page=1&size=10" "$ADMIN_TOKEN" "" "师傅列表(分页)"
api GET  "/master-profiles/2" "$ADMIN_TOKEN" "" "师傅详情(按userId)"
api GET  "/master-profiles/my" "$MASTER_TOKEN" "" "我的师傅档案"

# ── 师徒关系 ──
echo -e "\n${Y}▶ 师徒关系${N}"
api GET  "/relations/?page=1&size=10" "$ADMIN_TOKEN" "" "关系列表(管理员)"
api GET  "/relations/master" "$MASTER_TOKEN" "" "我的徒弟列表(师傅视角)"
api GET  "/relations/apprentice" "$APPRENTICE_TOKEN" "" "我的师傅列表(徒弟视角)"

# ── 教学任务 ──
echo -e "\n${Y}▶ 教学任务${N}"
api GET  "/tasks/?page=1&size=10" "$ADMIN_TOKEN" "" "任务列表(管理员)"
api GET  "/tasks/master?page=1&size=10" "$MASTER_TOKEN" "" "师傅的任务列表"
api GET  "/tasks/apprentice?page=1&size=10" "$APPRENTICE_TOKEN" "" "徒弟的任务列表"
api GET  "/tasks/1" "$ADMIN_TOKEN" "" "任务详情"

# ── 任务提交 ──
echo -e "\n${Y}▶ 任务提交${N}"
api GET  "/submissions/task/1" "$MASTER_TOKEN" "" "任务1的提交列表"
api GET  "/submissions/my/1" "$APPRENTICE_TOKEN" "" "我的任务1提交"

# ── 学习材料 ──
echo -e "\n${Y}▶ 学习材料${N}"
api GET  "/materials/?page=1&size=10" "$ADMIN_TOKEN" "" "材料列表(管理员)"
api GET  "/materials/my?page=1&size=10" "$MASTER_TOKEN" "" "我的材料列表(师傅)"
api GET  "/materials/task/1" "$MASTER_TOKEN" "" "任务1的关联材料"

# ── 作品 ──
echo -e "\n${Y}▶ 作品管理${N}"
api GET  "/artworks/?page=1&size=10" "$ADMIN_TOKEN" "" "作品列表(管理员)"
api GET  "/artworks/my?page=1&size=10" "$APPRENTICE_TOKEN" "" "我的作品列表(徒弟)"
api GET  "/artworks/master?page=1&size=10" "$MASTER_TOKEN" "" "待审作品列表(师傅)"
api GET  "/artworks/1" "$ADMIN_TOKEN" "" "作品详情"

# ── 作品点评 ──
echo -e "\n${Y}▶ 作品点评${N}"
api GET  "/artwork-reviews/artwork/1" "$ADMIN_TOKEN" "" "作品1的点评列表"

# ── 成长记录 ──
echo -e "\n${Y}▶ 成长记录${N}"
api GET  "/growth-records/my?page=1&size=10" "$APPRENTICE_TOKEN" "" "我的成长记录(徒弟)"
api GET  "/growth-records/apprentice/5?page=1&size=10" "$MASTER_TOKEN" "" "查看徒弟5的成长记录"

# ── 活动课程 ──
echo -e "\n${Y}▶ 活动课程${N}"
api GET  "/activities/?page=1&size=10" "$ADMIN_TOKEN" "" "活动列表(管理员)"
api GET  "/activities/1" "$ADMIN_TOKEN" "" "活动详情"

# ── 公告 ──
echo -e "\n${Y}▶ 公告管理${N}"
api GET  "/announcements?page=1&size=10" "$ADMIN_TOKEN" "" "公告列表"
api GET  "/announcements/published" "$ADMIN_TOKEN" "" "已发布公告列表"
api GET  "/announcements/1" "$ADMIN_TOKEN" "" "公告详情"

# ── 统计 ──
echo -e "\n${Y}▶ 数据统计${N}"
api GET  "/statistics/overview" "$ADMIN_TOKEN" "" "总览统计"
api GET  "/statistics/master-apprentice" "$ADMIN_TOKEN" "" "师徒规模统计"
api GET  "/statistics/category-artwork" "$ADMIN_TOKEN" "" "类别作品统计"
api GET  "/statistics/monthly-growth" "$ADMIN_TOKEN" "" "月度成长统计"
api GET  "/statistics/activity-participation" "$ADMIN_TOKEN" "" "活动参与统计"

# ── 审计日志 ──
echo -e "\n${Y}▶ 审计日志${N}"
api GET  "/audit-logs/?page=1&size=10" "$ADMIN_TOKEN" "" "审计日志列表"

# ── 结果汇总 ──
echo ""
echo -e "══════════════════════════════════════════════════"
echo -e "  测试结果: ${G}通过 ${PASS}${N} / ${R}失败 ${FAIL}${N} / 总计 ${TOTAL}"
echo -e "══════════════════════════════════════════════════"
[ $FAIL -eq 0 ] && echo -e "  ${G}🎉 全部接口测试通过！${N}" || echo -e "  ${R}⚠ 有 ${FAIL} 个接口失败，请检查${N}"
echo ""
