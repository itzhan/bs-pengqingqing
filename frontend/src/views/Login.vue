<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { NForm, NFormItem, NInput, NButton, NCard, useMessage } from 'naive-ui'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const message = useMessage()
const userStore = useUserStore()

const form = ref({ username: '', password: '' })
const loading = ref(false)

async function handleLogin() {
  if (!form.value.username || !form.value.password) {
    message.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    await userStore.login(form.value.username, form.value.password)
    message.success('登录成功')
    const redirect = route.query.redirect as string
    router.push(redirect || '/')
  } catch (e: any) {
    message.error(e.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <div class="login-left">
      <div class="login-brand">
        <div class="brand-icon">墨</div>
        <h1>非遗传承管理系统</h1>
        <p>传承千年技艺 · 守护文化根脉</p>
      </div>
      <div class="login-deco">
        <div class="deco-circle c1"></div>
        <div class="deco-circle c2"></div>
        <div class="deco-line"></div>
      </div>
    </div>
    <div class="login-right">
      <div class="login-form-wrapper">
        <h2>欢迎回来</h2>
        <p class="login-subtitle">登录你的账号，继续传承之旅</p>

        <n-form :model="form" @submit.prevent="handleLogin">
          <n-form-item label="用户名" path="username">
            <n-input
              v-model:value="form.username"
              placeholder="请输入用户名"
              size="large"
              @keyup.enter="handleLogin"
            />
          </n-form-item>
          <n-form-item label="密码" path="password">
            <n-input
              v-model:value="form.password"
              type="password"
              show-password-on="click"
              placeholder="请输入密码"
              size="large"
              @keyup.enter="handleLogin"
            />
          </n-form-item>
          <n-button
            type="primary"
            size="large"
            block
            :loading="loading"
            @click="handleLogin"
            style="margin-top: 8px;"
          >
            登 录
          </n-button>
        </n-form>

        <div class="login-footer">
          <span>还没有账号？</span>
          <router-link to="/register">立即注册</router-link>
        </div>

        <div class="test-accounts">
          <p>测试账号</p>
          <div class="account-tags">
            <span class="account-tag" @click="form.username='master1'; form.password='123456'">师傅: master1</span>
            <span class="account-tag" @click="form.username='apprentice1'; form.password='123456'">徒弟: apprentice1</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  display: flex; min-height: 100vh;
}
.login-left {
  flex: 1;
  background: var(--primary-dark);
  display: flex; flex-direction: column;
  align-items: center; justify-content: center;
  position: relative; overflow: hidden;
  padding: 48px;
}
.login-brand { position: relative; z-index: 1; text-align: center; }
.brand-icon {
  width: 72px; height: 72px; margin: 0 auto 24px;
  background: rgba(255,255,255,0.12);
  color: #fff;
  display: flex; align-items: center; justify-content: center;
  border-radius: 16px;
  font-family: var(--font-serif);
  font-size: 36px; font-weight: 700;
}
.login-brand h1 {
  color: #fff; font-family: var(--font-serif);
  font-size: 28px; margin-bottom: 8px;
}
.login-brand p {
  color: rgba(255,255,255,0.5); font-size: 15px;
}
.login-deco { position: absolute; inset: 0; }
.deco-circle {
  position: absolute; border-radius: 50%;
  border: 1px solid rgba(255,255,255,0.06);
}
.c1 { width: 400px; height: 400px; top: -100px; left: -100px; }
.c2 { width: 300px; height: 300px; bottom: -50px; right: -80px; }
.deco-line {
  position: absolute; top: 50%; left: 10%; right: 10%; height: 1px;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.08), transparent);
}

.login-right {
  flex: 1; display: flex;
  align-items: center; justify-content: center;
  padding: 48px;
  background: var(--bg-page);
}
.login-form-wrapper {
  width: 100%; max-width: 400px;
}
.login-form-wrapper h2 {
  font-family: var(--font-serif);
  font-size: 28px; margin-bottom: 6px;
  color: var(--text-primary);
}
.login-subtitle {
  color: var(--text-secondary); margin-bottom: 32px; font-size: 15px;
}
.login-footer {
  text-align: center; margin-top: 20px;
  font-size: 14px; color: var(--text-secondary);
}
.login-footer a { color: var(--primary); font-weight: 500; }
.test-accounts {
  margin-top: 32px; padding-top: 20px;
  border-top: 1px solid var(--border-light); text-align: center;
}
.test-accounts p { font-size: 12px; color: var(--text-secondary); margin-bottom: 8px; }
.account-tags { display: flex; gap: 8px; justify-content: center; flex-wrap: wrap; }
.account-tag {
  padding: 4px 12px; font-size: 12px;
  background: var(--primary-bg); color: var(--primary);
  border-radius: 4px; cursor: pointer;
  transition: background 0.2s;
}
.account-tag:hover { background: rgba(44,62,80,0.12); }

@media (max-width: 768px) {
  .login-page { flex-direction: column; }
  .login-left { padding: 48px 24px; min-height: 200px; }
}
</style>
