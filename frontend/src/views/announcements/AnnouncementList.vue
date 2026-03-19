<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { NTag, NEmpty } from 'naive-ui'
import { getPublishedAnnouncements } from '@/api/announcement'
import type { Announcement } from '@/types'

const router = useRouter()
const list = ref<Announcement[]>([])

onMounted(async () => {
  const res = await getPublishedAnnouncements()
  list.value = res.data || []
})

function formatDate(d: string) { return d ? new Date(d).toLocaleDateString('zh-CN') : '' }
</script>

<template>
  <div>
    <div class="page-header"><h1>系统公告</h1><p>重要通知与动态</p></div>
    <div class="container" style="max-width:800px;">
      <div v-if="list.length" class="ann-list">
        <div v-for="a in list" :key="a.id" class="ann-item card" @click="router.push(`/announcements/${a.id}`)">
          <div class="ann-item-head">
            <n-tag v-if="a.isTop" type="error" size="tiny" :bordered="false">置顶</n-tag>
            <h3>{{ a.title }}</h3>
          </div>
          <p>{{ a.content?.slice(0, 100) }}{{ (a.content?.length || 0) > 100 ? '...' : '' }}</p>
          <span class="ann-date">{{ formatDate(a.createdAt) }}</span>
        </div>
      </div>
      <n-empty v-else description="暂无公告" style="padding:80px 0;" />
    </div>
  </div>
</template>

<style scoped>
.ann-list { display: flex; flex-direction: column; gap: 16px; }
.ann-item { padding: 20px 24px; cursor: pointer; }
.ann-item-head { display: flex; gap: 8px; align-items: center; margin-bottom: 8px; }
.ann-item h3 { font-family: var(--font-serif); font-size: 16px; color: var(--text-primary); }
.ann-item p { font-size: 14px; color: var(--text-secondary); line-height: 1.6; margin-bottom: 8px; }
.ann-date { font-size: 12px; color: var(--text-secondary); }
</style>
