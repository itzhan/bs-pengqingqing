<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { NButton, NEmpty, NForm, NFormItem, NInput, NModal, NSelect, NTag, useMessage } from 'naive-ui'
import { getMyApprentices } from '@/api/relation'
import { createTask, getMasterTasks, updateTaskStatus } from '@/api/task'
import type { Relation, Task } from '@/types'

const route = useRoute()
const router = useRouter()
const message = useMessage()
const tasks = ref<Task[]>([])
const relations = ref<Relation[]>([])
const showCreate = ref(false)
const form = ref({
  title: '',
  description: '',
  deadline: '',
  apprenticeId: null as number | null
})

const apprenticeOptions = ref<any[]>([
  { label: '面向全部有效徒弟', value: null }
])

onMounted(async () => {
  await Promise.all([fetchRelations(), fetchData()])
  const queryApprenticeId = route.query.apprenticeId ? Number(route.query.apprenticeId) : null
  const queryApprenticeName = route.query.apprenticeName as string | undefined
  if (queryApprenticeId) {
    form.value.apprenticeId = queryApprenticeId
  }
  if (route.query.create === '1' || queryApprenticeId || queryApprenticeName) {
    showCreate.value = true
  }
})

async function fetchRelations() {
  const res = await getMyApprentices({ status: 1 })
  relations.value = res.data || []
  apprenticeOptions.value = [
    { label: '面向全部有效徒弟', value: null },
    ...relations.value.map((item) => ({
      label: `${item.apprenticeName || '未命名徒弟'} · ${item.projectName || '未关联传承项目'}`,
      value: item.apprenticeId
    }))
  ]
}

async function fetchData() {
  const res = await getMasterTasks({ page: 1, size: 100 })
  tasks.value = res.data?.records || []
}

function formatDeadline(deadline: string) {
  if (!deadline) return null
  if (deadline.includes('T')) return deadline
  return `${deadline}T23:59:59`
}

async function handleCreate() {
  if (!form.value.title) {
    message.warning('请输入任务标题')
    return
  }
  try {
    await createTask({
      ...form.value,
      deadline: formatDeadline(form.value.deadline)
    })
    message.success('任务已创建')
    showCreate.value = false
    form.value = { title: '', description: '', deadline: '', apprenticeId: null }
    fetchData()
  } catch (e: any) {
    message.error(e.message || '创建失败')
  }
}

async function handleComplete(id: number) {
  await updateTaskStatus(id, 1)
  message.success('任务已标记完成')
  fetchData()
}

function statusLabel(status: number) {
  return ['进行中', '已完成', '已取消'][status] || ''
}

function statusType(status: number): any {
  return ['warning', 'success', 'default'][status]
}

function taskTarget(task: Task) {
  return task.apprenticeName || '全部徒弟'
}
</script>

<template>
  <div class="container" style="max-width:900px; padding:32px 24px 64px;">
    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:24px;">
      <h2 style="font-family:var(--font-serif);">教学任务</h2>
      <n-button type="primary" @click="showCreate = true">+ 创建任务</n-button>
    </div>
    <div v-if="tasks.length" class="task-list">
      <div v-for="task in tasks" :key="task.id" class="card task-item" @click="router.push(`/workspace/master/tasks/${task.id}`)">
        <div class="task-head">
          <h3>{{ task.title }}</h3>
          <n-tag :type="statusType(task.status)" size="small" :bordered="false">{{ statusLabel(task.status) }}</n-tag>
        </div>
        <p v-if="task.description">{{ task.description.slice(0, 80) }}</p>
        <div class="task-meta">
          <span>布置对象：{{ taskTarget(task) }}</span>
          <span v-if="task.projectName">项目：{{ task.projectName }}</span>
          <span v-if="task.deadline">截止：{{ task.deadline.split('T')[0] }}</span>
          <n-button v-if="task.status === 0" size="tiny" @click.stop="handleComplete(task.id)">标记完成</n-button>
        </div>
      </div>
    </div>
    <n-empty v-else description="暂无任务，点击右上角创建" style="padding:80px 0;" />

    <n-modal v-model:show="showCreate" title="创建教学任务" preset="card" style="max-width:520px;">
      <n-form :model="form" label-placement="top">
        <n-form-item label="布置对象">
          <n-select v-model:value="form.apprenticeId" :options="apprenticeOptions" />
        </n-form-item>
        <n-form-item label="任务标题"><n-input v-model:value="form.title" /></n-form-item>
        <n-form-item label="任务描述"><n-input v-model:value="form.description" type="textarea" :rows="3" /></n-form-item>
        <n-form-item label="截止日期"><n-input v-model:value="form.deadline" placeholder="格式：2026-12-31" /></n-form-item>
        <n-button type="primary" block @click="handleCreate">创建</n-button>
      </n-form>
    </n-modal>
  </div>
</template>

<style scoped>
.task-list { display: flex; flex-direction: column; gap: 12px; }
.task-item { padding: 20px; cursor: pointer; }
.task-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px; }
.task-head h3 { font-family: var(--font-serif); font-size: 15px; color: var(--text-primary); }
.task-item p { font-size: 13px; color: var(--text-secondary); margin-bottom: 8px; }
.task-meta { display: flex; flex-wrap: wrap; gap: 12px; align-items: center; font-size: 12px; color: var(--text-secondary); }
</style>
