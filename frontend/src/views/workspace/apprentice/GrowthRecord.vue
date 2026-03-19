<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { NTag, NEmpty, NPagination, NTimeline, NTimelineItem } from 'naive-ui'
import { getMyGrowthRecords } from '@/api/growth'
import type { GrowthRecord } from '@/types'

const records = ref<GrowthRecord[]>([])
const total = ref(0)
const page = ref(1)

onMounted(fetchData)
async function fetchData() {
  const res = await getMyGrowthRecords({ page: page.value, size: 20 })
  records.value = res.data?.records || []
  total.value = res.data?.total || 0
}

function typeLabel(t: string) {
  const map: any = {
    MILESTONE: '成长里程碑',
    TASK_COMPLETE: '任务完成',
    ARTWORK_SUBMIT: '作品提交',
    ARTWORK_REVIEWED: '作品点评',
    COURSE_JOIN: '活动参与'
  }
  return map[t] || t
}
function formatDate(d: string) { return d ? new Date(d).toLocaleDateString('zh-CN') : '' }
</script>

<template>
  <div class="container" style="max-width:700px; padding:32px 24px 64px;">
    <h2 style="font-family:var(--font-serif); margin-bottom:24px;">成长记录</h2>
    <div v-if="records.length">
      <n-timeline>
        <n-timeline-item v-for="r in records" :key="r.id" :time="formatDate(r.createdAt)" :type="r.score && r.score >= 80 ? 'success' : 'default'">
          <template #header>
            <span style="font-weight:500;">{{ r.title }}</span>
            <n-tag size="tiny" :bordered="false" style="margin-left:8px;">{{ typeLabel(r.recordType) }}</n-tag>
            <n-tag v-if="r.score" size="tiny" type="warning" :bordered="false" style="margin-left:4px;">{{ r.score }}分</n-tag>
          </template>
          <p style="font-size:13px; color:var(--text-secondary);">{{ r.description }}</p>
        </n-timeline-item>
      </n-timeline>
      <div v-if="total > 20" style="display:flex; justify-content:center; margin-top:24px;">
        <n-pagination v-model:page="page" :page-count="Math.ceil(total / 20)" @update:page="fetchData" />
      </div>
    </div>
    <n-empty v-else description="暂无成长记录" style="padding:80px 0;" />
  </div>
</template>
