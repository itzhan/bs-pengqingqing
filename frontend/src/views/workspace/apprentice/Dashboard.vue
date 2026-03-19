<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { NButton, NEmpty, NTag, NTimeline, NTimelineItem } from 'naive-ui'
import { getMyArtworks } from '@/api/artwork'
import { getMyGrowthRecords } from '@/api/growth'
import { getMyMasters } from '@/api/relation'
import { getApprenticeTasks } from '@/api/task'
import type { Artwork, GrowthRecord, Relation, Task } from '@/types'

const router = useRouter()

const masters = ref<Relation[]>([])
const tasks = ref<Task[]>([])
const artworks = ref<Artwork[]>([])
const records = ref<GrowthRecord[]>([])

onMounted(async () => {
  const [masterRes, taskRes, artworkRes, growthRes] = await Promise.all([
    getMyMasters().catch(() => ({ data: [] })),
    getApprenticeTasks({ page: 1, size: 100 }).catch(() => ({ data: { records: [] } })),
    getMyArtworks({ page: 1, size: 100 }).catch(() => ({ data: { records: [] } })),
    getMyGrowthRecords({ page: 1, size: 6 }).catch(() => ({ data: { records: [] } }))
  ])

  masters.value = masterRes.data || []
  tasks.value = taskRes.data?.records || []
  artworks.value = artworkRes.data?.records || []
  records.value = growthRes.data?.records || []
})

const approvedMasters = computed(() => masters.value.filter((item) => item.status === 1))
const pendingApplications = computed(() => masters.value.filter((item) => item.status === 0))
const activeTasks = computed(() => tasks.value.filter((item) => item.status === 0))
const submittedArtworks = computed(() => artworks.value.filter((item) => item.status === 1))
const draftArtworks = computed(() => artworks.value.filter((item) => item.status === 0))
const recentTasks = computed(() => tasks.value.slice(0, 5))
const recentMasters = computed(() => masters.value.slice(0, 4))

function statCards() {
  return [
    { label: '已绑定师傅', value: approvedMasters.value.length, tone: 'primary' },
    { label: '待审核申请', value: pendingApplications.value.length, tone: 'accent' },
    { label: '待完成作业', value: activeTasks.value.length, tone: 'gold' },
    { label: '已提交作品', value: submittedArtworks.value.length, tone: 'primary' },
    { label: '作品草稿', value: draftArtworks.value.length, tone: 'ink' },
    { label: '成长记录', value: records.value.length, tone: 'accent' }
  ]
}

function relationStatusLabel(status: number) {
  return ['待审核', '已通过', '已拒绝', '已解除'][status] || ''
}

function taskStatusLabel(status: number) {
  return ['进行中', '已完成', '已取消'][status] || ''
}

function growthTypeLabel(type: string) {
  const map: Record<string, string> = {
    MILESTONE: '成长里程碑',
    TASK_COMPLETE: '任务完成',
    ARTWORK_SUBMIT: '作品提交',
    ARTWORK_REVIEWED: '作品点评',
    COURSE_JOIN: '活动参与'
  }
  return map[type] || type
}

function formatDate(date: string) {
  return date ? new Date(date).toLocaleDateString('zh-CN') : ''
}
</script>

<template>
  <div class="workspace-page container">
    <section class="workspace-hero apprentice-hero">
      <div>
        <p class="workspace-kicker">徒弟工作台</p>
        <h1>跟着师傅学、按时交作业、持续上传作品，成长轨迹会自然沉淀下来。</h1>
        <p class="workspace-desc">
          这里整合了你的师傅关系、学习任务、作品草稿和成长记录。可以先去看最近作业，再补交作品或继续拜师。
        </p>
      </div>
      <div class="workspace-actions">
        <n-button type="primary" @click="router.push('/workspace/apprentice/tasks')">进入作业区</n-button>
        <n-button @click="router.push('/workspace/apprentice/artworks?create=1')">新建作品</n-button>
        <n-button @click="router.push('/masters')">继续寻找师傅</n-button>
      </div>
    </section>

    <section class="stats-grid">
      <div v-for="card in statCards()" :key="card.label" class="stat-card" :data-tone="card.tone">
        <span class="stat-label">{{ card.label }}</span>
        <strong class="stat-value">{{ card.value }}</strong>
      </div>
    </section>

    <section class="workspace-grid">
      <div class="panel card">
        <div class="panel-head">
          <div>
            <h2>我的师傅</h2>
            <p>查看关系状态，进入对应的学习任务与成长路线。</p>
          </div>
          <n-button text @click="router.push('/workspace/apprentice/masters')">全部关系</n-button>
        </div>
        <div v-if="recentMasters.length" class="master-list">
          <div v-for="relation in recentMasters" :key="relation.id" class="master-item">
            <div>
              <strong>{{ relation.masterName || '未命名师傅' }}</strong>
              <p>{{ relation.projectName || '未关联传承项目' }}</p>
            </div>
            <div class="item-actions">
              <n-tag size="small" :bordered="false" :type="relation.status === 1 ? 'success' : relation.status === 0 ? 'warning' : 'default'">
                {{ relationStatusLabel(relation.status) }}
              </n-tag>
              <n-button v-if="relation.status === 1" size="small" @click="router.push('/workspace/apprentice/tasks')">查看作业</n-button>
              <n-button v-else size="small" tertiary @click="router.push(`/masters/${relation.masterId}`)">查看详情</n-button>
            </div>
          </div>
        </div>
        <n-empty v-else description="还没有师徒关系记录" style="padding:48px 0;" />
      </div>

      <div class="panel card">
        <div class="panel-head">
          <div>
            <h2>最近作业</h2>
            <p>优先处理进行中的作业，提交后可等待师傅点评。</p>
          </div>
          <n-button text @click="router.push('/workspace/apprentice/tasks')">作业中心</n-button>
        </div>
        <div v-if="recentTasks.length" class="task-list">
          <div v-for="task in recentTasks" :key="task.id" class="task-item" @click="router.push(`/workspace/apprentice/tasks/${task.id}`)">
            <div>
              <strong>{{ task.title }}</strong>
              <p>师傅：{{ task.masterName || '未命名师傅' }}</p>
            </div>
            <div class="task-meta">
              <n-tag size="small" :bordered="false" :type="task.status === 0 ? 'warning' : 'success'">{{ taskStatusLabel(task.status) }}</n-tag>
              <span v-if="task.deadline">{{ task.deadline.split('T')[0] }}</span>
            </div>
          </div>
        </div>
        <n-empty v-else description="当前没有分配给你的作业" style="padding:48px 0;" />
      </div>
    </section>

    <section class="workspace-grid lower-grid">
      <div class="panel card">
        <div class="panel-head">
          <div>
            <h2>最近成长记录</h2>
            <p>每次交作业、提交作品和被点评都会形成成长轨迹。</p>
          </div>
          <n-button text @click="router.push('/workspace/apprentice/growth')">全部记录</n-button>
        </div>
        <div v-if="records.length" class="timeline-wrap">
          <n-timeline>
            <n-timeline-item v-for="record in records" :key="record.id" :time="formatDate(record.createdAt)">
              <template #header>
                <span style="font-weight:500;">{{ record.title }}</span>
                <n-tag size="tiny" :bordered="false" style="margin-left:8px;">{{ growthTypeLabel(record.recordType) }}</n-tag>
              </template>
              <p class="timeline-desc">{{ record.description }}</p>
            </n-timeline-item>
          </n-timeline>
        </div>
        <n-empty v-else description="还没有成长记录" style="padding:48px 0;" />
      </div>

      <div class="panel card">
        <div class="panel-head">
          <div>
            <h2>快捷操作</h2>
            <p>把最常用的学习操作放在首页，减少跳转成本。</p>
          </div>
        </div>
        <div class="quick-grid">
          <button class="quick-card" @click="router.push('/workspace/apprentice/tasks')">
            <strong>提交作业</strong>
            <span>进入学习任务页，提交文字说明和附件</span>
          </button>
          <button class="quick-card" @click="router.push('/workspace/apprentice/artworks?create=1')">
            <strong>上传作品</strong>
            <span>先保存草稿，再选择合适时机提交</span>
          </button>
          <button class="quick-card" @click="router.push('/workspace/apprentice/growth')">
            <strong>查看成长记录</strong>
            <span>把任务完成、作品点评和里程碑串起来看</span>
          </button>
          <button class="quick-card" @click="router.push('/masters')">
            <strong>继续寻找师傅</strong>
            <span>如果关系结束，可重新发起拜师申请</span>
          </button>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.workspace-page {
  padding-top: 28px;
  padding-bottom: 64px;
}

.workspace-hero {
  display: flex;
  justify-content: space-between;
  gap: 24px;
  padding: 28px 30px;
  border-radius: 24px;
  margin-bottom: 24px;
  color: #fff;
  background:
    linear-gradient(135deg, rgba(86, 42, 24, 0.95), rgba(149, 91, 56, 0.92)),
    radial-gradient(circle at top right, rgba(255, 228, 181, 0.22), transparent 38%);
}

.workspace-kicker {
  font-size: 12px;
  letter-spacing: 0.14em;
  text-transform: uppercase;
  opacity: 0.76;
  margin-bottom: 10px;
}

.workspace-hero h1 {
  color: #fff;
  font-size: 32px;
  max-width: 680px;
  margin-bottom: 12px;
}

.workspace-desc {
  max-width: 680px;
  font-size: 14px;
  color: rgba(255,255,255,0.8);
}

.workspace-actions {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 12px;
  min-width: 180px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 14px;
  margin-bottom: 24px;
}

.stat-card {
  padding: 18px;
  border-radius: 18px;
  background: var(--bg-card);
  border: 1px solid var(--border-light);
  box-shadow: var(--shadow-sm);
}

.stat-card[data-tone='accent'] { background: linear-gradient(180deg, #fff4ef, #fff); }
.stat-card[data-tone='primary'] { background: linear-gradient(180deg, #f3f6f8, #fff); }
.stat-card[data-tone='gold'] { background: linear-gradient(180deg, #fff8ed, #fff); }
.stat-card[data-tone='ink'] { background: linear-gradient(180deg, #f7f7f5, #fff); }

.stat-label {
  display: block;
  font-size: 12px;
  color: var(--text-secondary);
  margin-bottom: 10px;
}

.stat-value {
  font-family: var(--font-serif);
  font-size: 30px;
  color: var(--text-primary);
}

.workspace-grid {
  display: grid;
  grid-template-columns: 1.08fr 0.92fr;
  gap: 18px;
  margin-bottom: 18px;
}

.panel {
  padding: 22px;
}

.panel-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
  margin-bottom: 18px;
}

.panel-head h2 {
  font-size: 20px;
  margin-bottom: 4px;
}

.panel-head p {
  font-size: 13px;
  color: var(--text-secondary);
}

.master-list,
.task-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.master-item,
.task-item {
  display: flex;
  justify-content: space-between;
  gap: 14px;
  padding: 14px 16px;
  border-radius: 16px;
  background: #fbfaf7;
  border: 1px solid var(--border-light);
}

.task-item {
  cursor: pointer;
}

.master-item strong,
.task-item strong {
  color: var(--text-primary);
  font-size: 15px;
}

.master-item p,
.task-item p {
  font-size: 12px;
  color: var(--text-secondary);
  margin-top: 4px;
}

.item-actions,
.task-meta {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  align-items: center;
  gap: 8px;
}

.task-meta span {
  font-size: 12px;
  color: var(--text-secondary);
}

.timeline-wrap {
  padding-right: 8px;
}

.timeline-desc {
  font-size: 12px;
  color: var(--text-secondary);
}

.quick-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.quick-card {
  text-align: left;
  padding: 16px;
  border-radius: 16px;
  border: 1px solid var(--border-light);
  background: linear-gradient(180deg, #fff, #f8f5ef);
  cursor: pointer;
  transition: transform 0.2s var(--ease), box-shadow 0.2s var(--ease);
}

.quick-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.quick-card strong {
  display: block;
  color: var(--text-primary);
  font-size: 15px;
  margin-bottom: 6px;
}

.quick-card span {
  font-size: 12px;
  color: var(--text-secondary);
  line-height: 1.7;
}

@media (max-width: 1024px) {
  .stats-grid {
    grid-template-columns: repeat(3, 1fr);
  }

  .workspace-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .workspace-hero {
    flex-direction: column;
  }

  .workspace-actions {
    min-width: auto;
  }

  .stats-grid,
  .quick-grid {
    grid-template-columns: 1fr;
  }

  .master-item,
  .task-item {
    flex-direction: column;
  }

  .item-actions,
  .task-meta {
    justify-content: flex-start;
  }
}
</style>
