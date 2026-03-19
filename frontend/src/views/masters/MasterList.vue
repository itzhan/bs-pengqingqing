<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { NInput, NPagination, NEmpty, NAvatar } from 'naive-ui'
import { getMasterProfiles } from '@/api/master'
import type { MasterProfile } from '@/types'

const router = useRouter()
const masters = ref<MasterProfile[]>([])
const total = ref(0)
const page = ref(1)
const loading = ref(false)

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const res = await getMasterProfiles({ page: page.value, size: 12, auditStatus: 1 })
    masters.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
}
</script>

<template>
  <div>
    <div class="page-header">
      <h1>传承师傅</h1>
      <p>寻找你的传艺恩师</p>
    </div>
    <div class="container">
      <div v-if="masters.length" class="master-grid">
        <div v-for="m in masters" :key="m.id" class="card master-card" @click="router.push(`/masters/${m.userId}`)">
          <div class="master-avatar">
            <n-avatar :size="72" round :src="m.avatar || undefined">{{ m.realName?.charAt(0) || '师' }}</n-avatar>
          </div>
          <h3>{{ m.realName || m.title || '传承师傅' }}</h3>
          <p class="master-title" v-if="m.title">{{ m.title }}</p>
          <p class="master-specialties" v-if="m.specialties">擅长：{{ m.specialties }}</p>
          <div class="master-meta">
            <span v-if="m.careerYears">从艺 {{ m.careerYears }} 年</span>
          </div>
        </div>
      </div>
      <n-empty v-else description="暂无师傅数据" style="padding:80px 0;" />
      <div class="pagination-wrap" v-if="total > 12">
        <n-pagination v-model:page="page" :page-count="Math.ceil(total / 12)" @update:page="fetchData" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.master-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 24px; }
.master-card { padding: 28px 20px; text-align: center; cursor: pointer; }
.master-avatar { margin-bottom: 14px; }
.master-card h3 { font-family: var(--font-serif); font-size: 16px; color: var(--text-primary); margin-bottom: 4px; }
.master-title { font-size: 13px; color: var(--accent); margin-bottom: 6px; }
.master-specialties { font-size: 12px; color: var(--text-secondary); margin-bottom: 8px; }
.master-meta { font-size: 12px; color: var(--text-secondary); }
.pagination-wrap { display: flex; justify-content: center; margin-top: 40px; padding-bottom: 40px; }
@media (max-width: 768px) { .master-grid { grid-template-columns: repeat(2, 1fr); } }
</style>
