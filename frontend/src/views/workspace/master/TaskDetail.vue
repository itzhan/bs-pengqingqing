<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { NButton, NTag, NEmpty, NInput, NInputNumber, useMessage } from 'naive-ui'
import { getTaskDetail, getTaskSubmissions, reviewSubmission } from '@/api/task'
import { getTaskMaterials } from '@/api/material'
import type { Task, Submission, Material } from '@/types'

const route = useRoute()
const router = useRouter()
const message = useMessage()
const task = ref<Task | null>(null)
const submissions = ref<Submission[]>([])
const materials = ref<Material[]>([])

onMounted(async () => {
  const id = Number(route.params.id)
  const [t, s, m] = await Promise.all([
    getTaskDetail(id),
    getTaskSubmissions(id).catch(() => ({ data: [] })),
    getTaskMaterials(id).catch(() => ({ data: { records: [] } }))
  ])
  task.value = t.data
  submissions.value = s.data || []
  materials.value = m.data?.records || m.data || []
})

const reviewForm = ref<{ [key: number]: { masterComment: string; score: number } }>({})

async function handleReview(id: number) {
  const r = reviewForm.value[id]
  if (!r) return
  try {
    await reviewSubmission(id, r)
    message.success('点评成功')
    const res = await getTaskSubmissions(Number(route.params.id))
    submissions.value = res.data || []
  } catch (e: any) { message.error(e.message || '失败') }
}
</script>

<template>
  <div class="container" style="max-width:800px; padding:32px 24px 64px;">
    <n-button text @click="router.back()" style="margin-bottom:20px;">← 返回</n-button>
    <template v-if="task">
      <h2 style="font-family:var(--font-serif); margin-bottom:8px;">{{ task.title }}</h2>
      <p style="color:var(--text-secondary); margin-bottom:24px;">{{ task.description }}</p>
      <div style="display:flex; gap:12px; flex-wrap:wrap; margin-bottom:20px; font-size:13px; color:var(--text-secondary);">
        <span>布置对象：{{ task.apprenticeName || '全部徒弟' }}</span>
        <span v-if="task.projectName">项目：{{ task.projectName }}</span>
        <n-button
          v-if="task.apprenticeId"
          size="small"
          tertiary
          @click="router.push(`/workspace/master/apprentices/${task.apprenticeId}/growth?name=${encodeURIComponent(task.apprenticeName || '')}`)"
        >
          查看成长记录
        </n-button>
      </div>

      <h3 style="font-family:var(--font-serif); margin-bottom:12px;">提交记录</h3>
      <div v-if="submissions.length" class="sub-list">
        <div v-for="s in submissions" :key="s.id" class="card sub-item" style="padding:20px; margin-bottom:12px;">
          <p><strong>{{ s.apprenticeName || '未命名徒弟' }}</strong>：{{ s.content }}</p>
          <div v-if="s.fileUrl"><a :href="s.fileUrl" target="_blank">查看附件</a></div>
          <div v-if="s.masterComment" style="margin-top:8px; padding:12px; background:var(--primary-bg); border-radius:8px;">
            <p style="font-size:13px; color:var(--primary);">师傅点评：{{ s.masterComment }}（{{ s.score }}分）</p>
          </div>
          <div v-else style="margin-top:12px;">
            <n-input v-model:value="(reviewForm[s.id] ??= { masterComment: '', score: 80 }).masterComment" placeholder="输入点评" style="margin-bottom:8px;" />
            <div style="display:flex; gap:8px; align-items:center;">
              <span style="font-size:13px;">评分：</span>
              <n-input-number v-model:value="(reviewForm[s.id] ??= { masterComment: '', score: 80 }).score" :min="0" :max="100" style="width:120px;" />
              <n-button type="primary" size="small" @click="handleReview(s.id)">提交点评</n-button>
            </div>
          </div>
        </div>
      </div>
      <n-empty v-else description="暂无提交" />
    </template>
  </div>
</template>
