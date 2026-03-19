<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { NButton, NCard, NEmpty, NForm, NFormItem, NInput, NModal, NPagination, NSelect, NTag, NTimeline, NTimelineItem, useMessage } from 'naive-ui'
import { createApprenticeGrowthRecord, getApprenticeGrowthRecords } from '@/api/growth'
import type { GrowthRecord } from '@/types'

const route = useRoute()
const router = useRouter()
const message = useMessage()
const apprenticeName = ref((route.query.name as string) || '')
const records = ref<GrowthRecord[]>([])
const total = ref(0)
const page = ref(1)
const submitLoading = ref(false)
const modalVisible = ref(false)
const formData = ref({
  recordType: 'MILESTONE',
  title: '',
  description: ''
})
const typeOptions = [
  { label: '成长里程碑', value: 'MILESTONE' },
  { label: '任务完成', value: 'TASK_COMPLETE' },
  { label: '作品提交', value: 'ARTWORK_SUBMIT' },
  { label: '作品点评', value: 'ARTWORK_REVIEWED' },
  { label: '活动参与', value: 'COURSE_JOIN' }
]

onMounted(fetchData)

async function fetchData() {
  const res = await getApprenticeGrowthRecords(Number(route.params.id), { page: page.value, size: 20 })
  records.value = res.data?.records || []
  total.value = res.data?.total || 0
}

async function handleCreateRecord() {
  if (!formData.value.title.trim()) {
    message.warning('请输入标题')
    return
  }
  submitLoading.value = true
  try {
    await createApprenticeGrowthRecord(Number(route.params.id), {
      recordType: formData.value.recordType,
      title: formData.value.title.trim(),
      description: formData.value.description.trim()
    })
    message.success('新增成长记录成功')
    modalVisible.value = false
    formData.value = { recordType: 'MILESTONE', title: '', description: '' }
    fetchData()
  } finally {
    submitLoading.value = false
  }
}

function typeLabel(type: string) {
  const map: Record<string, string> = {
    MILESTONE: '成长里程碑',
    TASK_COMPLETE: '任务完成',
    ARTWORK_SUBMIT: '作品提交',
    ARTWORK_REVIEWED: '作品点评',
    COURSE_JOIN: '活动参与'
  }
  return map[type] || type
}

function formatDate(date: string) {
  return date ? new Date(date).toLocaleDateString('zh-CN') : ''
}
</script>

<template>
  <div class="container" style="max-width:760px; padding:32px 24px 64px;">
    <div style="display:flex; align-items:center; justify-content:space-between; margin-bottom:20px;">
      <n-button text @click="router.back()">← 返回</n-button>
      <n-button type="primary" @click="modalVisible = true">新增成长记录</n-button>
    </div>
    <h2 style="font-family:var(--font-serif); margin-bottom:8px;">{{ apprenticeName || '未命名徒弟' }} 的成长记录</h2>
    <p style="font-size:13px; color:var(--text-secondary); margin-bottom:24px;">可用于跟踪作业完成、作品提交和里程碑变化。</p>
    <div v-if="records.length">
      <n-timeline>
        <n-timeline-item
          v-for="record in records"
          :key="record.id"
          :time="formatDate(record.createdAt)"
          :type="record.score && record.score >= 80 ? 'success' : 'default'"
        >
          <template #header>
            <span style="font-weight:500;">{{ record.title }}</span>
            <n-tag size="tiny" :bordered="false" style="margin-left:8px;">{{ typeLabel(record.recordType) }}</n-tag>
          </template>
          <p style="font-size:13px; color:var(--text-secondary);">{{ record.description }}</p>
        </n-timeline-item>
      </n-timeline>
      <div v-if="total > 20" style="display:flex; justify-content:center; margin-top:24px;">
        <n-pagination v-model:page="page" :page-count="Math.ceil(total / 20)" @update:page="fetchData" />
      </div>
    </div>
    <n-empty v-else description="该徒弟暂无成长记录" style="padding:80px 0;" />

    <n-modal v-model:show="modalVisible">
      <n-card style="width:560px" title="新增成长记录" :bordered="false" role="dialog" aria-modal="true">
        <n-form :model="formData" label-placement="left" :label-width="90">
          <n-form-item label="记录类型">
            <n-select v-model:value="formData.recordType" :options="typeOptions" />
          </n-form-item>
          <n-form-item label="标题">
            <n-input v-model:value="formData.title" placeholder="请输入标题" />
          </n-form-item>
          <n-form-item label="描述">
            <n-input v-model:value="formData.description" type="textarea" placeholder="可选" />
          </n-form-item>
        </n-form>
        <template #footer>
          <div style="display:flex; justify-content:flex-end; gap:12px;">
            <n-button @click="modalVisible = false">取消</n-button>
            <n-button type="primary" :loading="submitLoading" @click="handleCreateRecord">保存</n-button>
          </div>
        </template>
      </n-card>
    </n-modal>
  </div>
</template>
