<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { NButton, NDropdown, NAvatar, NIcon, NBadge } from 'naive-ui'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const navLinks = [
  { label: '首页', path: '/' },
  { label: '非遗项目', path: '/projects' },
  { label: '传承师傅', path: '/masters' },
  { label: '活动课程', path: '/activities' },
  { label: '系统公告', path: '/announcements' },
]

const isActive = (path: string) => {
  if (path === '/') return route.path === '/'
  return route.path.startsWith(path)
}

const workspaceLink = computed(() => {
  if (!userStore.isLoggedIn) return null
  if (userStore.isMaster) return { label: '师傅工作台', path: '/workspace/master/dashboard' }
  if (userStore.isApprentice) return { label: '徒弟工作台', path: '/workspace/apprentice/dashboard' }
  return null
})

const workspaceTabs = computed(() => {
  if (!route.path.startsWith('/workspace')) {
    return []
  }
  if (userStore.isMaster) {
    return [
      { label: '工作台首页', path: '/workspace/master/dashboard' },
      { label: '我的徒弟', path: '/workspace/master/apprentices' },
      { label: '教学任务', path: '/workspace/master/tasks' },
      { label: '徒弟作品', path: '/workspace/master/artworks' },
      { label: '学习材料', path: '/workspace/master/materials' },
      { label: '我的档案', path: '/workspace/master/profile' }
    ]
  }
  if (userStore.isApprentice) {
    return [
      { label: '工作台首页', path: '/workspace/apprentice/dashboard' },
      { label: '我的师傅', path: '/workspace/apprentice/masters' },
      { label: '学习任务', path: '/workspace/apprentice/tasks' },
      { label: '我的作品', path: '/workspace/apprentice/artworks' },
      { label: '成长记录', path: '/workspace/apprentice/growth' },
      { label: '个人中心', path: '/profile' }
    ]
  }
  return []
})

function isWorkspaceTabActive(path: string) {
  if (path === '/profile') {
    return route.path === '/profile'
  }
  return route.path === path || route.path.startsWith(`${path}/`)
}

const userMenuOptions = [
  { label: '个人中心', key: 'profile' },
  { label: '退出登录', key: 'logout' },
]

function handleUserMenu(key: string) {
  if (key === 'profile') router.push('/profile')
  if (key === 'logout') {
    userStore.logout()
    router.push('/')
  }
}
</script>

<template>
  <div class="layout">
    <!-- 顶部导航 -->
    <header class="navbar">
      <div class="container navbar-inner">
        <router-link to="/" class="logo">
          <span class="logo-icon">墨</span>
          <span class="logo-text">非遗传承</span>
        </router-link>

        <nav class="nav-links">
          <router-link
            v-for="link in navLinks"
            :key="link.path"
            :to="link.path"
            :class="['nav-link', { active: isActive(link.path) }]"
          >
            {{ link.label }}
          </router-link>
          <router-link
            v-if="workspaceLink"
            :to="workspaceLink.path"
            :class="['nav-link workspace-link', { active: route.path.startsWith('/workspace') }]"
          >
            {{ workspaceLink.label }}
          </router-link>
        </nav>

        <div class="nav-actions">
          <template v-if="userStore.isLoggedIn">
            <n-dropdown :options="userMenuOptions" @select="handleUserMenu" trigger="click">
              <div class="user-avatar-btn">
                <n-avatar :size="34" round :src="userStore.user?.avatar || undefined">
                  {{ userStore.user?.realName?.charAt(0) || '用' }}
                </n-avatar>
                <span class="user-name">{{ userStore.user?.realName }}</span>
              </div>
            </n-dropdown>
          </template>
          <template v-else>
            <n-button quaternary size="small" @click="router.push('/login')">登录</n-button>
            <n-button type="primary" size="small" @click="router.push('/register')">注册</n-button>
          </template>
        </div>
      </div>
    </header>

    <div v-if="workspaceTabs.length" class="workspace-strip">
      <div class="container workspace-strip-inner">
        <span class="workspace-title">{{ workspaceLink?.label }}</span>
        <div class="workspace-tabs">
          <router-link
            v-for="tab in workspaceTabs"
            :key="tab.path"
            :to="tab.path"
            :class="['workspace-tab', { active: isWorkspaceTabActive(tab.path) }]"
          >
            {{ tab.label }}
          </router-link>
        </div>
      </div>
    </div>

    <!-- 主体 -->
    <main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="container footer-inner">
        <div class="footer-brand">
          <span class="logo-icon">墨</span>
          <div>
            <p class="footer-title">非遗技艺师徒档案传承管理系统</p>
            <p class="footer-sub">传承千年技艺 · 守护文化根脉</p>
          </div>
        </div>
        <div class="footer-links">
          <div class="footer-col">
            <h4>快速导航</h4>
            <router-link to="/projects">非遗项目</router-link>
            <router-link to="/masters">传承师傅</router-link>
            <router-link to="/activities">活动课程</router-link>
          </div>
          <div class="footer-col">
            <h4>关于系统</h4>
            <router-link to="/announcements">系统公告</router-link>
          </div>
        </div>
        <div class="footer-bottom">
          <p>© 2026 非遗传承管理系统 · 毕业设计作品</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<style scoped>
.layout { display: flex; flex-direction: column; min-height: 100vh; }

/* ── Navbar ── */
.navbar {
  position: sticky; top: 0; z-index: 100;
  background: rgba(255,255,255,0.92);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--border-light);
  height: 64px;
}
.navbar-inner {
  display: flex; align-items: center; justify-content: space-between;
  height: 100%;
}
.logo {
  display: flex; align-items: center; gap: 10px;
  text-decoration: none;
}
.logo-icon {
  width: 36px; height: 36px;
  background: var(--primary);
  color: #fff;
  display: flex; align-items: center; justify-content: center;
  border-radius: 8px;
  font-family: var(--font-serif);
  font-size: 18px; font-weight: 700;
}
.logo-text {
  font-family: var(--font-serif);
  font-size: 18px; font-weight: 600;
  color: var(--text-primary);
}
.nav-links {
  display: flex; gap: 4px;
}
.nav-link {
  padding: 6px 14px;
  font-size: 14px;
  color: var(--text-body);
  border-radius: 6px;
  transition: all 0.2s var(--ease);
  text-decoration: none;
}
.nav-link:hover { color: var(--primary); background: var(--primary-bg); }
.nav-link.active { color: var(--primary); font-weight: 500; background: var(--primary-bg); }
.workspace-link { color: var(--accent); }
.workspace-link:hover, .workspace-link.active { color: var(--accent); background: var(--accent-soft); }

.nav-actions { display: flex; align-items: center; gap: 8px; }
.user-avatar-btn {
  display: flex; align-items: center; gap: 8px;
  cursor: pointer; padding: 4px 8px;
  border-radius: 8px;
  transition: background 0.2s;
}
.user-avatar-btn:hover { background: var(--primary-bg); }
.user-name { font-size: 14px; color: var(--text-primary); font-weight: 500; }

/* ── Main ── */
.main-content { flex: 1; }

.workspace-strip {
  position: sticky;
  top: 64px;
  z-index: 90;
  background: rgba(250,248,245,0.94);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid var(--border-light);
}

.workspace-strip-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  min-height: 56px;
}

.workspace-title {
  font-family: var(--font-serif);
  font-size: 15px;
  color: var(--text-primary);
  white-space: nowrap;
}

.workspace-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: flex-end;
}

.workspace-tab {
  padding: 6px 12px;
  font-size: 13px;
  border-radius: 999px;
  color: var(--text-body);
  text-decoration: none;
  transition: all 0.2s var(--ease);
}

.workspace-tab:hover {
  color: var(--primary);
  background: var(--primary-bg);
}

.workspace-tab.active {
  color: #fff;
  background: var(--primary);
}

/* ── Footer ── */
.footer {
  background: var(--primary-dark);
  color: rgba(255,255,255,0.7);
  padding: 48px 0 0;
  margin-top: 64px;
}
.footer-inner { }
.footer-brand {
  display: flex; align-items: center; gap: 14px;
  margin-bottom: 32px;
}
.footer-brand .logo-icon { background: rgba(255,255,255,0.15); }
.footer-title { color: #fff; font-size: 16px; font-weight: 600; font-family: var(--font-serif); }
.footer-sub { font-size: 13px; margin-top: 2px; }
.footer-links { display: flex; gap: 64px; margin-bottom: 32px; }
.footer-col h4 { color: #fff; font-size: 14px; margin-bottom: 12px; font-family: var(--font-serif); }
.footer-col a {
  display: block; font-size: 13px;
  color: rgba(255,255,255,0.6);
  margin-bottom: 8px; text-decoration: none;
  transition: color 0.2s;
}
.footer-col a:hover { color: #fff; }
.footer-bottom {
  border-top: 1px solid rgba(255,255,255,0.1);
  padding: 16px 0;
  text-align: center;
  font-size: 12px;
}

@media (max-width: 768px) {
  .workspace-strip-inner {
    align-items: flex-start;
    flex-direction: column;
    padding-top: 10px;
    padding-bottom: 10px;
  }

  .workspace-tabs {
    width: 100%;
    justify-content: flex-start;
  }
}
</style>
