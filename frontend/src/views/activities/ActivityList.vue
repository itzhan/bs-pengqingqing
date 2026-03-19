<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { NPagination, NTag, NEmpty } from 'naive-ui'
import { getActivities } from '@/api/activity'
import type { Activity } from '@/types'

const router = useRouter()
const list = ref<Activity[]>([])
const total = ref(0)
const page = ref(1)

onMounted(() => fetchData())

async function fetchData() {
  const res = await getActivities({ page: page.value, size: 9 })
  list.value = res.data?.records || []
  total.value = res.data?.total || 0
}

function statusLabel(s: number) { return ['未开始', '进行中', '已结束'][s] || '' }
function statusType(s: number): any { return ['default', 'success', 'info'][s] || 'default' }
function formatDate(d: string) { return d ? new Date(d).toLocaleDateString('zh-CN') : '' }
</script>

<template>
  <div>
    <div class="page-header"><h1>活动课程</h1><p>参与非遗文化体验活动</p></div>
    <div class="container">
      <div v-if="list.length" class="act-grid">
        <div v-for="a in list" :key="a.id" class="card act-card" @click="router.push(`/activities/${a.id}`)">
          <div class="act-cover"><img :src="a.coverUrl || '/placeholder-activity.svg'" :alt="a.title" /></div>
          <div class="act-body">
            <div class="act-head">
              <n-tag :type="statusType(a.status)" size="tiny" :bordered="false">{{ statusLabel(a.status) }}</n-tag>
            </div>
            <h3>{{ a.title }}</h3>
            <p>{{ a.description?.slice(0, 60) }}</p>
            <div class="act-meta">
              <span>📍 {{ a.location || '线上' }}</span>
              <span>📅 {{ formatDate(a.startTime) }}</span>
            </div>
          </div>
        </div>
      </div>
      <n-empty v-else description="暂无活动" style="padding:80px 0;" />
      <div class="pagination-wrap" v-if="total > 9">
        <n-pagination v-model:page="page" :page-count="Math.ceil(total / 9)" @update:page="fetchData" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.act-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 24px; }
.act-card { cursor: pointer; }
.act-cover { height: 180px; overflow: hidden; background: var(--border-light); }
.act-cover img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.4s; }
.act-card:hover .act-cover img { transform: scale(1.05); }
.act-body { padding: 16px; }
.act-head { margin-bottom: 6px; }
.act-body h3 { font-family: var(--font-serif); font-size: 16px; color: var(--text-primary); margin-bottom: 6px; }
.act-body p { font-size: 13px; color: var(--text-secondary); margin-bottom: 10px; }
.act-meta { display: flex; gap: 16px; font-size: 12px; color: var(--text-secondary); }
.pagination-wrap { display: flex; justify-content: center; margin-top: 40px; padding-bottom: 40px; }
@media (max-width: 768px) { .act-grid { grid-template-columns: 1fr; } }
</style>
