<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { NForm, NFormItem, NInput, NInputNumber, NButton, useMessage } from 'naive-ui'
import { getMyMasterProfile, saveMasterProfile } from '@/api/master'

const message = useMessage()
const form = ref({
  title: '', heritageProjectId: null as number | null, skillCategoryId: null as number | null,
  careerYears: 0, careerHistory: '', specialties: '', representativeWorks: '',
  bio: '', honor: ''
})
const loading = ref(false)
const hasProfile = ref(false)

onMounted(async () => {
  try {
    const res = await getMyMasterProfile()
    if (res.data) {
      Object.assign(form.value, res.data)
      hasProfile.value = true
    }
  } catch {}
})

async function handleSave() {
  loading.value = true
  try {
    await saveMasterProfile(form.value)
    message.success('档案已保存')
    hasProfile.value = true
  } catch (e: any) { message.error(e.message || '保存失败') }
  finally { loading.value = false }
}
</script>

<template>
  <div class="container" style="max-width:720px; padding:32px 24px 64px;">
    <h2 style="font-family:var(--font-serif); margin-bottom:24px;">{{ hasProfile ? '编辑档案' : '创建我的档案' }}</h2>
    <div class="card" style="padding:28px;">
      <n-form :model="form" label-placement="top">
        <n-form-item label="头衔/称号"><n-input v-model:value="form.title" placeholder="如：国家级非遗传承人" /></n-form-item>
        <n-form-item label="从艺年数"><n-input-number v-model:value="form.careerYears" :min="0" style="width:100%;" /></n-form-item>
        <n-form-item label="擅长领域"><n-input v-model:value="form.specialties" placeholder="如：陶瓷制作、釉彩工艺" /></n-form-item>
        <n-form-item label="荣誉称号"><n-input v-model:value="form.honor" placeholder="如：工艺美术大师" /></n-form-item>
        <n-form-item label="个人简介"><n-input v-model:value="form.bio" type="textarea" :rows="3" /></n-form-item>
        <n-form-item label="从艺经历"><n-input v-model:value="form.careerHistory" type="textarea" :rows="4" /></n-form-item>
        <n-button type="primary" :loading="loading" @click="handleSave">保存档案</n-button>
      </n-form>
    </div>
  </div>
</template>
