<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { NButton, NTag, useMessage } from 'naive-ui'
import { getActivity, participateActivity, cancelParticipation, getParticipants } from '@/api/activity'
import { useUserStore } from '@/stores/user'
import type { Activity } from '@/types'

const route = useRoute()
const router = useRouter()
const message = useMessage()
const userStore = useUserStore()
const activity = ref<Activity | null>(null)
const participantCount = ref(0)
const hasJoined = ref(false)

onMounted(async () => {
  const res = await getActivity(Number(route.params.id))
  activity.value = res.data
  try {
    const pRes = await getParticipants(Number(route.params.id))
    const list = pRes.data || []
    participantCount.value = list.length
    hasJoined.value = list.some((p: any) => p.userId === userStore.user?.id)
  } catch {}
})

async function handleJoin() {
  if (!userStore.isLoggedIn) { router.push('/login'); return }
  try {
    await participateActivity(Number(route.params.id))
    message.success('报名成功')
    hasJoined.value = true
    participantCount.value++
  } catch (e: any) { message.error(e.message || '报名失败') }
}

async function handleCancel() {
  try {
    await cancelParticipation(Number(route.params.id))
    message.success('已取消报名')
    hasJoined.value = false
    participantCount.value--
  } catch (e: any) { message.error(e.message || '取消失败') }
}

function formatDate(d: string) { return d ? new Date(d).toLocaleString('zh-CN') : '' }
function statusLabel(s: number) { return ['未开始', '进行中', '已结束'][s] || '' }
</script>

<template>
  <div class="container" style="padding-top:32px; padding-bottom:64px;">
    <n-button text @click="router.back()" style="margin-bottom:24px;">← 返回</n-button>
    <template v-if="activity">
      <div class="act-detail-cover" v-if="activity.coverUrl">
        <img :src="activity.coverUrl" :alt="activity.title" />
      </div>
      <h1 class="act-detail-title">{{ activity.title }}</h1>
      <div class="act-detail-tags">
        <n-tag :bordered="false" size="small">{{ statusLabel(activity.status) }}</n-tag>
        <span>📍 {{ activity.location || '线上' }}</span>
        <span>📅 {{ formatDate(activity.startTime) }} ~ {{ formatDate(activity.endTime) }}</span>
        <span>👥 {{ participantCount }} / {{ activity.maxParticipants || '∞' }}</span>
      </div>
      <div class="act-detail-actions" v-if="activity.status === 0">
        <n-button v-if="!hasJoined" type="primary" @click="handleJoin">立即报名</n-button>
        <n-button v-else @click="handleCancel">取消报名</n-button>
      </div>
      <div class="act-detail-content">
        <h3>活动简介</h3>
        <p>{{ activity.description }}</p>
        <h3 v-if="activity.content">活动详情</h3>
        <div v-if="activity.content" style="white-space:pre-wrap;">{{ activity.content }}</div>
      </div>
    </template>
  </div>
</template>

<style scoped>
.act-detail-cover { height: 320px; border-radius: var(--radius-lg); overflow: hidden; margin-bottom: 24px; background: var(--border-light); }
.act-detail-cover img { width: 100%; height: 100%; object-fit: cover; }
.act-detail-title { font-family: var(--font-serif); font-size: 28px; margin-bottom: 12px; }
.act-detail-tags { display: flex; gap: 16px; align-items: center; font-size: 14px; color: var(--text-secondary); margin-bottom: 20px; flex-wrap: wrap; }
.act-detail-actions { margin-bottom: 32px; }
.act-detail-content { background: var(--bg-card); padding: 28px; border-radius: var(--radius-lg); border: 1px solid var(--border-light); }
.act-detail-content h3 { font-family: var(--font-serif); margin-bottom: 12px; margin-top: 24px; }
.act-detail-content h3:first-child { margin-top: 0; }
.act-detail-content p, .act-detail-content div { font-size: 15px; line-height: 1.8; color: var(--text-body); }
</style>
