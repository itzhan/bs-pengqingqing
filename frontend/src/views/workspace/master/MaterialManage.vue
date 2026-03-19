<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { NButton, NEmpty, NModal, NForm, NFormItem, NInput, useMessage } from 'naive-ui'
import { getMyMaterials, uploadMaterial, deleteMaterial } from '@/api/material'
import { uploadFile } from '@/api/file'
import type { Material } from '@/types'

const message = useMessage()
const route = useRoute()
const materials = ref<Material[]>([])
const showCreate = ref(false)
const form = ref({ title: '', description: '', fileUrl: '', fileType: '', fileSize: 0 })

onMounted(async () => {
  await fetchData()
  if (route.query.create === '1') {
    showCreate.value = true
  }
})
async function fetchData() {
  const res = await getMyMaterials({ page: 1, size: 100 })
  materials.value = res.data?.records || []
}

async function handleUpload(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return
  try {
    const res = await uploadFile(file)
    form.value.fileUrl = res.data
    form.value.fileType = file.name.split('.').pop() || ''
    form.value.fileSize = file.size
    message.success('文件已上传')
  } catch { message.error('上传失败') }
}

async function handleCreate() {
  if (!form.value.title || !form.value.fileUrl) { message.warning('请填写标题并上传文件'); return }
  try {
    await uploadMaterial(form.value)
    message.success('材料已发布')
    showCreate.value = false
    form.value = { title: '', description: '', fileUrl: '', fileType: '', fileSize: 0 }
    fetchData()
  } catch (e: any) { message.error(e.message || '失败') }
}

async function handleDelete(id: number) {
  try { await deleteMaterial(id); message.success('已删除'); fetchData() }
  catch (e: any) { message.error(e.message || '删除失败') }
}
</script>

<template>
  <div class="container" style="max-width:800px; padding:32px 24px 64px;">
    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:24px;">
      <h2 style="font-family:var(--font-serif);">学习材料</h2>
      <n-button type="primary" @click="showCreate = true">+ 上传材料</n-button>
    </div>
    <div v-if="materials.length" class="mat-list">
      <div v-for="m in materials" :key="m.id" class="card mat-item" style="padding:16px; margin-bottom:12px;">
        <div style="display:flex; justify-content:space-between; align-items:center;">
          <div>
            <h3 style="font-size:15px; color:var(--text-primary);">{{ m.title }}</h3>
            <p style="font-size:12px; color:var(--text-secondary);">{{ m.fileType?.toUpperCase() }} · {{ (m.fileSize / 1024).toFixed(0) }}KB</p>
          </div>
          <div style="display:flex; gap:8px;">
            <a :href="m.fileUrl" target="_blank"><n-button size="small">下载</n-button></a>
            <n-button size="small" @click="handleDelete(m.id)">删除</n-button>
          </div>
        </div>
      </div>
    </div>
    <n-empty v-else description="暂无材料" style="padding:80px 0;" />

    <n-modal v-model:show="showCreate" title="上传学习材料" preset="card" style="max-width:500px;">
      <n-form :model="form" label-placement="top">
        <n-form-item label="材料标题"><n-input v-model:value="form.title" /></n-form-item>
        <n-form-item label="描述"><n-input v-model:value="form.description" type="textarea" :rows="2" /></n-form-item>
        <n-form-item label="上传文件">
          <label style="display:inline-block; padding:6px 16px; border:1px dashed var(--border); border-radius:6px; cursor:pointer; font-size:14px; color:var(--primary);">
            {{ form.fileUrl ? '已上传' : '选择文件' }}
            <input type="file" @change="handleUpload" hidden />
          </label>
        </n-form-item>
        <n-button type="primary" block @click="handleCreate">发布</n-button>
      </n-form>
    </n-modal>
  </div>
</template>
