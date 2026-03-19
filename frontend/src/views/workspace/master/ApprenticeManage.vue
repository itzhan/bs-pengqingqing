<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { NAvatar, NButton, NEmpty, NTabPane, NTabs, NTag, useMessage } from 'naive-ui'
import { approveRelation, dissolveRelation, getMyApprentices } from '@/api/relation'
import type { Relation } from '@/types'

const router = useRouter()
const message = useMessage()
const relations = ref<Relation[]>([])
const activeTab = ref('1')

onMounted(fetchData)

async function fetchData() {
  const res = await getMyApprentices()
  relations.value = res.data || []
}

const filteredRelations = computed(() => {
  if (activeTab.value === 'all') {
    return relations.value
  }
  return relations.value.filter((item) => String(item.status) === activeTab.value)
})

async function handleApprove(id: number, approved: boolean) {
  try {
    await approveRelation(id, approved)
    message.success(approved ? '已通过申请' : '已拒绝申请')
    fetchData()
  } catch (e: any) {
    message.error(e.message || '操作失败')
  }
}

async function handleDissolve(id: number) {
  const reason = window.prompt('请输入解除原因（选填）', '') || ''
  try {
    await dissolveRelation(id, reason)
    message.success('已解除关系')
    fetchData()
  } catch (e: any) {
    message.error(e.message || '操作失败')
  }
}

function statusLabel(status: number) {
  return ['待审核', '已通过', '已拒绝', '已解除'][status] || ''
}

function statusType(status: number): any {
  return ['warning', 'success', 'error', 'default'][status]
}
</script>

<template>
  <div class="container" style="max-width:900px; padding:32px 24px 64px;">
    <h2 style="font-family:var(--font-serif); margin-bottom:24px;">我的徒弟</h2>

    <n-tabs v-model:value="activeTab" type="line" animated style="margin-bottom:20px;">
      <n-tab-pane name="0" tab="待审核申请" />
      <n-tab-pane name="1" tab="已绑定徒弟" />
      <n-tab-pane name="2" tab="已拒绝" />
      <n-tab-pane name="3" tab="已解除" />
      <n-tab-pane name="all" tab="全部记录" />
    </n-tabs>

    <div v-if="filteredRelations.length" class="card" style="padding:20px;">
      <div v-for="relation in filteredRelations" :key="relation.id" class="relation-item">
        <div class="relation-info">
          <div class="relation-main">
            <n-avatar :size="42" round :src="relation.apprenticeAvatar || undefined">
              {{ (relation.apprenticeName || '徒').charAt(0) }}
            </n-avatar>
            <div>
              <span class="relation-name">徒弟 {{ relation.apprenticeName || '未命名徒弟' }}</span>
              <p class="relation-meta">意向项目：{{ relation.projectName || '未关联传承项目' }}</p>
              <p v-if="relation.applyReason" class="relation-meta">申请说明：{{ relation.applyReason }}</p>
            </div>
          </div>
          <n-tag :type="statusType(relation.status)" size="small" :bordered="false">
            {{ statusLabel(relation.status) }}
          </n-tag>
        </div>

        <div class="relation-actions">
          <template v-if="relation.status === 0">
            <n-button size="small" type="primary" @click="handleApprove(relation.id, true)">通过</n-button>
            <n-button size="small" @click="handleApprove(relation.id, false)">拒绝</n-button>
          </template>
          <template v-if="relation.status === 1">
            <n-button
              size="small"
              type="primary"
              tertiary
              @click="router.push(`/workspace/master/tasks?apprenticeId=${relation.apprenticeId}&apprenticeName=${encodeURIComponent(relation.apprenticeName || '')}`)"
            >
              布置作业
            </n-button>
            <n-button
              size="small"
              tertiary
              @click="router.push(`/workspace/master/apprentices/${relation.apprenticeId}/growth?name=${encodeURIComponent(relation.apprenticeName || '')}`)"
            >
              成长记录
            </n-button>
            <n-button size="small" @click="handleDissolve(relation.id)">解除关系</n-button>
          </template>
        </div>
      </div>
    </div>

    <n-empty v-else description="当前分类下暂无记录" style="padding:80px 0;" />
  </div>
</template>

<style scoped>
.relation-item {
  padding: 16px 0;
  border-bottom: 1px solid var(--border-light);
}

.relation-item:last-child {
  border-bottom: none;
}

.relation-info {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.relation-main {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.relation-name {
  font-weight: 500;
  color: var(--text-primary);
}

.relation-meta {
  font-size: 12px;
  color: var(--text-secondary);
  margin-top: 4px;
}

.relation-actions {
  display: flex;
  gap: 8px;
  margin-top: 10px;
}
</style>
