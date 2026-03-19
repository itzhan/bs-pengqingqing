<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { NAvatar, NButton, NEmpty, NTabPane, NTabs, NTag, useMessage } from 'naive-ui'
import { cancelRelation, dissolveRelation, getMyMasters } from '@/api/relation'
import type { Relation } from '@/types'

const router = useRouter()
const message = useMessage()
const masters = ref<Relation[]>([])
const activeTab = ref('all')

onMounted(fetchData)

async function fetchData() {
  const res = await getMyMasters()
  masters.value = res.data || []
}

const filteredMasters = computed(() => {
  if (activeTab.value === 'all') {
    return masters.value
  }
  return masters.value.filter((item) => String(item.status) === activeTab.value)
})

async function handleCancel(relation: Relation) {
  try {
    await cancelRelation(relation.id)
    message.success('申请已撤回')
    fetchData()
  } catch (e: any) {
    message.error(e.message || '撤回失败')
  }
}

async function handleDissolve(relation: Relation) {
  const reason = window.prompt('请输入解除原因（选填）', '') || ''
  try {
    await dissolveRelation(relation.id, reason)
    message.success('师徒关系已解除')
    fetchData()
  } catch (e: any) {
    message.error(e.message || '解除失败')
  }
}

function statusLabel(status: number) {
  return ['待审核', '已通过', '已拒绝', '已解除'][status] || ''
}

function statusType(status: number): any {
  return ['warning', 'success', 'error', 'default'][status]
}

function relationTip(status: number) {
  if (status === 0) return '申请已提交，等待师傅审核'
  if (status === 1) return '关系已生效，可参与任务与作品传承'
  if (status === 2) return '申请被驳回，可调整后再次申请'
  return '关系已结束，可重新寻找合适的师傅'
}
</script>

<template>
  <div class="container" style="max-width:800px; padding:32px 24px 64px;">
    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:24px;">
      <h2 style="font-family:var(--font-serif);">我的师傅</h2>
      <n-button @click="router.push('/masters')">寻找师傅</n-button>
    </div>

    <n-tabs v-model:value="activeTab" type="line" animated style="margin-bottom:20px;">
      <n-tab-pane name="all" tab="全部" />
      <n-tab-pane name="0" tab="待审核" />
      <n-tab-pane name="1" tab="已绑定" />
      <n-tab-pane name="2" tab="已拒绝" />
      <n-tab-pane name="3" tab="已解除" />
    </n-tabs>

    <div v-if="filteredMasters.length" class="master-list">
      <div v-for="relation in filteredMasters" :key="relation.id" class="card relation-card">
        <div class="relation-header">
          <div class="relation-main">
            <n-avatar :size="42" round :src="relation.masterAvatar || undefined">
              {{ (relation.masterName || '师').charAt(0) }}
            </n-avatar>
            <div>
              <div class="relation-name" @click="router.push(`/masters/${relation.masterId}`)">
                师傅 {{ relation.masterName || '未命名师傅' }}
              </div>
              <p class="relation-meta">传承项目：{{ relation.projectName || '未关联传承项目' }}</p>
              <p v-if="relation.applyReason" class="relation-meta">申请说明：{{ relation.applyReason }}</p>
            </div>
          </div>
          <n-tag :type="statusType(relation.status)" size="small" :bordered="false">
            {{ statusLabel(relation.status) }}
          </n-tag>
        </div>

        <div class="relation-footer">
          <span class="relation-tip">{{ relationTip(relation.status) }}</span>
          <div class="relation-actions">
            <n-button v-if="relation.status === 0" size="small" @click="handleCancel(relation)">撤回申请</n-button>
            <n-button v-if="relation.status === 1" size="small" @click="handleDissolve(relation)">解除关系</n-button>
            <n-button
              v-if="relation.status === 2 || relation.status === 3"
              size="small"
              type="primary"
              @click="router.push(`/masters/${relation.masterId}`)"
            >
              再次申请
            </n-button>
          </div>
        </div>
      </div>
    </div>

    <n-empty v-else description="还没有拜师记录，去寻找你的师傅吧" style="padding:80px 0;" />
  </div>
</template>

<style scoped>
.relation-card {
  padding: 20px;
  margin-bottom: 12px;
}

.relation-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.relation-main {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.relation-name {
  font-weight: 500;
  color: var(--text-primary);
  cursor: pointer;
}

.relation-name:hover {
  color: var(--primary);
}

.relation-meta {
  margin-top: 4px;
  font-size: 12px;
  color: var(--text-secondary);
}

.relation-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: 14px;
}

.relation-tip {
  font-size: 12px;
  color: var(--text-secondary);
}

.relation-actions {
  display: flex;
  gap: 8px;
}
</style>
