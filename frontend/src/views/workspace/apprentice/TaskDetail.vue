<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { NButton, NInput, NEmpty, useMessage } from 'naive-ui'
import { getTaskDetail, getMySubmission, submitTask } from '@/api/task'
import { getTaskMaterials } from '@/api/material'
import { uploadFile } from '@/api/file'
import type { Task, Submission, Material } from '@/types'

const route = useRoute()
const router = useRouter()
const message = useMessage()
const task = ref<Task | null>(null)
const submission = ref<Submission | null>(null)
const materials = ref<Material[]>([])
const form = ref({ content: '', fileUrl: '' })

onMounted(async () => {
  const id = Number(route.params.id)
  const [t, s, m] = await Promise.all([
    getTaskDetail(id),
    getMySubmission(id).catch(() => ({ data: null })),
    getTaskMaterials(id).catch(() => ({ data: { records: [] } }))
  ])
  task.value = t.data
  submission.value = s.data
  materials.value = m.data?.records || m.data || []
})

async function handleUpload(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return
  try {
    const res = await uploadFile(file)
    form.value.fileUrl = res.data
    message.success('文件已上传')
  } catch { message.error('上传失败') }
}

async function handleSubmit() {
  if (!form.value.content) { message.warning('请输入提交内容'); return }
  try {
    await submitTask({ taskId: Number(route.params.id), ...form.value })
    message.success('提交成功')
    const res = await getMySubmission(Number(route.params.id))
    submission.value = res.data
  } catch (e: any) { message.error(e.message || '提交失败') }
}
</script>

<template>
  <div class="container" style="max-width:800px; padding:32px 24px 64px;">
    <n-button text @click="router.back()" style="margin-bottom:20px;">← 返回</n-button>
    <template v-if="task">
      <h2 style="font-family:var(--font-serif); margin-bottom:8px;">{{ task.title }}</h2>
      <p style="color:var(--text-secondary); margin-bottom:24px; white-space:pre-wrap;">{{ task.description }}</p>
      <div style="display:flex; gap:12px; flex-wrap:wrap; margin-bottom:20px; font-size:13px; color:var(--text-secondary);">
        <span>师傅：{{ task.masterName || '未命名师傅' }}</span>
        <span>布置对象：{{ task.apprenticeName || '全部徒弟' }}</span>
        <span v-if="task.projectName">项目：{{ task.projectName }}</span>
      </div>

      <div v-if="materials.length" class="card" style="padding:20px; margin-bottom:20px;">
        <h3 style="font-size:15px; margin-bottom:8px;">学习材料</h3>
        <div v-for="m in materials" :key="m.id" style="margin-bottom:6px;">
          <a :href="m.fileUrl" target="_blank" style="font-size:14px;">📄 {{ m.title }}</a>
        </div>
      </div>

      <div v-if="submission" class="card" style="padding:20px;">
        <h3 style="font-size:15px; margin-bottom:8px;">我的提交</h3>
        <p>{{ submission.content }}</p>
        <div v-if="submission.fileUrl"><a :href="submission.fileUrl" target="_blank">附件</a></div>
        <div v-if="submission.masterComment" style="margin-top:12px; padding:12px; background:var(--primary-bg); border-radius:8px;">
          <p style="font-size:13px; color:var(--primary);">师傅点评：{{ submission.masterComment }}（{{ submission.score }}分）</p>
        </div>
      </div>
      <div v-else class="card" style="padding:20px;">
        <h3 style="font-size:15px; margin-bottom:12px;">提交作业</h3>
        <n-input v-model:value="form.content" type="textarea" :rows="3" placeholder="输入学习心得或成果" style="margin-bottom:12px;" />
        <label style="display:inline-block; padding:6px 16px; border:1px dashed var(--border); border-radius:6px; cursor:pointer; font-size:14px; color:var(--primary); margin-bottom:12px;">
          {{ form.fileUrl ? '已上传附件' : '上传附件（选填）' }}
          <input type="file" @change="handleUpload" hidden />
        </label>
        <div><n-button type="primary" @click="handleSubmit">提交</n-button></div>
      </div>
    </template>
  </div>
</template>
