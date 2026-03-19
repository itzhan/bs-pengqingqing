<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { NForm, NFormItem, NInput, NButton, NRadioGroup, NRadio, NSelect, useMessage } from 'naive-ui'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const message = useMessage()
const userStore = useUserStore()

const form = ref({
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  phone: '',
  email: '',
  gender: 1,
  role: 'apprentice' as 'master' | 'apprentice',
})
const loading = ref(false)

const roleOptions = [
  { label: '我要学艺（徒弟）', value: 'apprentice' },
  { label: '我要授艺（师傅）', value: 'master' },
]

async function handleRegister() {
  if (!form.value.username || !form.value.password || !form.value.realName) {
    message.warning('请填写必填信息')
    return
  }
  if (form.value.password !== form.value.confirmPassword) {
    message.warning('两次密码不一致')
    return
  }
  loading.value = true
  try {
    const { confirmPassword, ...data } = form.value
    await userStore.register(data)
    message.success('注册成功，请登录')
    router.push('/login')
  } catch (e: any) {
    message.error(e.message || '注册失败')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="register-page">
    <div class="register-left">
      <div class="register-brand">
        <div class="brand-icon">墨</div>
        <h1>加入非遗传承</h1>
        <p>选择你的角色，开始传承之旅</p>
      </div>
    </div>
    <div class="register-right">
      <div class="register-form-wrapper">
        <h2>创建账号</h2>
        <p class="register-subtitle">填写以下信息完成注册</p>

        <n-form :model="form" @submit.prevent="handleRegister">
          <n-form-item label="我的角色" path="role">
            <n-radio-group v-model:value="form.role">
              <n-radio v-for="opt in roleOptions" :key="opt.value" :value="opt.value">
                {{ opt.label }}
              </n-radio>
            </n-radio-group>
          </n-form-item>
          <n-form-item label="用户名" path="username">
            <n-input v-model:value="form.username" placeholder="设置登录用户名" />
          </n-form-item>
          <n-form-item label="真实姓名" path="realName">
            <n-input v-model:value="form.realName" placeholder="你的真实姓名" />
          </n-form-item>
          <n-form-item label="密码" path="password">
            <n-input v-model:value="form.password" type="password" show-password-on="click" placeholder="设置密码" />
          </n-form-item>
          <n-form-item label="确认密码" path="confirmPassword">
            <n-input v-model:value="form.confirmPassword" type="password" show-password-on="click" placeholder="再次输入密码" />
          </n-form-item>
          <n-form-item label="手机号" path="phone">
            <n-input v-model:value="form.phone" placeholder="选填" />
          </n-form-item>
          <n-form-item label="邮箱" path="email">
            <n-input v-model:value="form.email" placeholder="选填" />
          </n-form-item>
          <n-form-item label="性别" path="gender">
            <n-radio-group v-model:value="form.gender">
              <n-radio :value="1">男</n-radio>
              <n-radio :value="0">女</n-radio>
            </n-radio-group>
          </n-form-item>
          <n-button type="primary" size="large" block :loading="loading" @click="handleRegister" style="margin-top: 8px;">
            注 册
          </n-button>
        </n-form>

        <div class="register-footer">
          <span>已有账号？</span>
          <router-link to="/login">立即登录</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.register-page { display: flex; min-height: 100vh; }
.register-left {
  width: 380px; flex-shrink: 0;
  background: var(--primary-dark);
  display: flex; align-items: center; justify-content: center;
  position: relative; overflow: hidden; padding: 48px;
}
.register-brand { text-align: center; position: relative; z-index: 1; }
.brand-icon {
  width: 64px; height: 64px; margin: 0 auto 20px;
  background: rgba(255,255,255,0.12);
  color: #fff; display: flex; align-items: center; justify-content: center;
  border-radius: 14px; font-family: var(--font-serif);
  font-size: 32px; font-weight: 700;
}
.register-brand h1 { color: #fff; font-family: var(--font-serif); font-size: 24px; margin-bottom: 8px; }
.register-brand p { color: rgba(255,255,255,0.5); font-size: 14px; }
.register-right {
  flex: 1; display: flex; align-items: center; justify-content: center;
  padding: 48px; background: var(--bg-page); overflow-y: auto;
}
.register-form-wrapper { width: 100%; max-width: 460px; }
.register-form-wrapper h2 { font-family: var(--font-serif); font-size: 28px; margin-bottom: 6px; }
.register-subtitle { color: var(--text-secondary); margin-bottom: 28px; font-size: 15px; }
.register-footer {
  text-align: center; margin-top: 20px;
  font-size: 14px; color: var(--text-secondary);
}
.register-footer a { color: var(--primary); font-weight: 500; }

@media (max-width: 768px) {
  .register-page { flex-direction: column; }
  .register-left { width: 100%; min-height: 160px; padding: 32px; }
}
</style>
