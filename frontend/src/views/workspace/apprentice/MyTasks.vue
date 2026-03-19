<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { NTag, NEmpty } from 'naive-ui'
import { getApprenticeTasks } from '@/api/task'
import type { Task } from '@/types'

const router = useRouter()
const tasks = ref<Task[]>([])

onMounted(async () => {
  const res = await getApprenticeTasks({ page: 1, size: 100 })
  tasks.value = res.data?.records || []
})

function statusLabel(s: number) { return ['进行中', '已完成', '已取消'][s] || '' }
function statusType(s: number): any { return ['warning', 'success', 'default'][s] }
</script>

<template>
  <div class="container" style="max-width:800px; padding:32px 24px 64px;">
    <h2 style="font-family:var(--font-serif); margin-bottom:24px;">学习任务</h2>
    <div v-if="tasks.length" class="task-list">
      <div v-for="t in tasks" :key="t.id" class="card task-item" @click="router.push(`/workspace/apprentice/tasks/${t.id}`)">
        <div class="task-head">
          <h3>{{ t.title }}</h3>
          <n-tag :type="statusType(t.status)" size="small" :bordered="false">{{ statusLabel(t.status) }}</n-tag>
        </div>
        <p v-if="t.description">{{ t.description.slice(0, 80) }}</p>
        <div class="task-meta">
          <span>师傅：{{ t.masterName || '未命名师傅' }}</span>
          <span>布置对象：{{ t.apprenticeName || '全部徒弟' }}</span>
          <span v-if="t.projectName">项目：{{ t.projectName }}</span>
          <span v-if="t.deadline">截止：{{ t.deadline.split('T')[0] }}</span>
        </div>
      </div>
    </div>
    <n-empty v-else description="暂无任务" style="padding:80px 0;" />
  </div>
</template>

<style scoped>
.task-list { display: flex; flex-direction: column; gap: 12px; }
.task-item { padding: 20px; cursor: pointer; }
.task-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px; }
.task-head h3 { font-family: var(--font-serif); font-size: 15px; color: var(--text-primary); }
.task-item p { font-size: 13px; color: var(--text-secondary); margin-bottom: 8px; }
.task-meta { font-size: 12px; color: var(--text-secondary); }
</style>
