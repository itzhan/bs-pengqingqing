<script setup lang="ts">
import { ref } from 'vue'
import { NForm, NFormItem, NInput, NButton, NAvatar, NRadioGroup, NRadio, NTabs, NTabPane, useMessage } from 'naive-ui'
import { useUserStore } from '@/stores/user'
import { uploadFile } from '@/api/file'

const message = useMessage()
const userStore = useUserStore()

const profileForm = ref({
  realName: userStore.user?.realName || '',
  phone: userStore.user?.phone || '',
  email: userStore.user?.email || '',
  gender: userStore.user?.gender ?? 1,
  avatar: userStore.user?.avatar || '',
})

const passwordForm = ref({ oldPassword: '', newPassword: '', confirmPassword: '' })
const saving = ref(false)

async function handleUploadAvatar(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return
  try {
    const res = await uploadFile(file)
    profileForm.value.avatar = res.data
    message.success('头像上传成功')
  } catch { message.error('上传失败') }
}

async function saveProfile() {
  saving.value = true
  try {
    await userStore.updateProfile(profileForm.value)
    message.success('资料已更新')
  } catch (e: any) { message.error(e.message || '保存失败') }
  finally { saving.value = false }
}

async function savePassword() {
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    message.warning('两次密码不一致'); return
  }
  try {
    await userStore.changePassword(passwordForm.value.oldPassword, passwordForm.value.newPassword)
    message.success('密码已修改')
    passwordForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
  } catch (e: any) { message.error(e.message || '修改失败') }
}
</script>

<template>
  <div class="container" style="max-width:640px; padding-top:32px; padding-bottom:64px;">
    <h1 class="page-title" style="font-family:var(--font-serif); font-size:24px; margin-bottom:24px;">个人中心</h1>
    <n-tabs type="line">
      <n-tab-pane name="profile" tab="基本资料">
        <div class="profile-form card" style="padding:28px; margin-top:16px;">
          <div class="avatar-section">
            <n-avatar :size="72" round :src="profileForm.avatar || undefined">{{ profileForm.realName?.charAt(0) }}</n-avatar>
            <label class="avatar-upload">
              更换头像
              <input type="file" accept="image/*" @change="handleUploadAvatar" hidden />
            </label>
          </div>
          <n-form :model="profileForm" label-placement="left" label-width="80px" style="margin-top:20px;">
            <n-form-item label="姓名"><n-input v-model:value="profileForm.realName" /></n-form-item>
            <n-form-item label="手机"><n-input v-model:value="profileForm.phone" /></n-form-item>
            <n-form-item label="邮箱"><n-input v-model:value="profileForm.email" /></n-form-item>
            <n-form-item label="性别">
              <n-radio-group v-model:value="profileForm.gender">
                <n-radio :value="1">男</n-radio><n-radio :value="0">女</n-radio>
              </n-radio-group>
            </n-form-item>
            <n-button type="primary" :loading="saving" @click="saveProfile">保存修改</n-button>
          </n-form>
        </div>
      </n-tab-pane>
      <n-tab-pane name="password" tab="修改密码">
        <div class="card" style="padding:28px; margin-top:16px;">
          <n-form :model="passwordForm" label-placement="left" label-width="90px">
            <n-form-item label="当前密码"><n-input v-model:value="passwordForm.oldPassword" type="password" show-password-on="click" /></n-form-item>
            <n-form-item label="新密码"><n-input v-model:value="passwordForm.newPassword" type="password" show-password-on="click" /></n-form-item>
            <n-form-item label="确认密码"><n-input v-model:value="passwordForm.confirmPassword" type="password" show-password-on="click" /></n-form-item>
            <n-button type="primary" @click="savePassword">修改密码</n-button>
          </n-form>
        </div>
      </n-tab-pane>
    </n-tabs>
  </div>
</template>

<style scoped>
.avatar-section { display: flex; align-items: center; gap: 16px; }
.avatar-upload {
  font-size: 13px; color: var(--primary); cursor: pointer;
  padding: 4px 12px; border: 1px solid var(--border); border-radius: 6px;
  transition: background 0.2s;
}
.avatar-upload:hover { background: var(--primary-bg); }
</style>
