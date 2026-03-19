<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { NButton } from 'naive-ui'
import { getAnnouncement } from '@/api/announcement'
import type { Announcement } from '@/types'

const route = useRoute()
const router = useRouter()
const item = ref<Announcement | null>(null)

onMounted(async () => {
  const res = await getAnnouncement(Number(route.params.id))
  item.value = res.data
})

function formatDate(d: string) { return d ? new Date(d).toLocaleString('zh-CN') : '' }
</script>

<template>
  <div class="container" style="max-width:800px; padding-top:32px; padding-bottom:64px;">
    <n-button text @click="router.back()" style="margin-bottom:24px;">← 返回</n-button>
    <template v-if="item">
      <h1 style="font-family:var(--font-serif); font-size:28px; margin-bottom:8px;">{{ item.title }}</h1>
      <p style="font-size:13px; color:var(--text-secondary); margin-bottom:32px;">发布时间：{{ formatDate(item.createdAt) }}</p>
      <div class="ann-content card" style="padding:32px;">
        <div style="white-space:pre-wrap; font-size:15px; line-height:1.8; color:var(--text-body);">{{ item.content }}</div>
      </div>
    </template>
  </div>
</template>
