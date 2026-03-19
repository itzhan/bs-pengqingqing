<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { NButton, NEmpty, NTag } from 'naive-ui'
import { getMasterArtworks } from '@/api/artwork'
import { getMyMaterials } from '@/api/material'
import { getMyApprentices } from '@/api/relation'
import { getMasterTasks } from '@/api/task'
import type { Artwork, Material, Relation, Task } from '@/types'

const router = useRouter()

const relations = ref<Relation[]>([])
const tasks = ref<Task[]>([])
const artworks = ref<Artwork[]>([])
const materials = ref<Material[]>([])

onMounted(async () => {
  const [relationRes, taskRes, artworkRes, materialRes] = await Promise.all([
    getMyApprentices().catch(() => ({ data: [] })),
    getMasterTasks({ page: 1, size: 100 }).catch(() => ({ data: { records: [] } })),
    getMasterArtworks({ page: 1, size: 100 }).catch(() => ({ data: { records: [] } })),
    getMyMaterials({ page: 1, size: 100 }).catch(() => ({ data: { records: [] } }))
  ])

  relations.value = relationRes.data || []
  tasks.value = taskRes.data?.records || []
  artworks.value = artworkRes.data?.records || []
  materials.value = materialRes.data?.records || []
})

const approvedRelations = computed(() => relations.value.filter((item) => item.status === 1))
const pendingRelations = computed(() => relations.value.filter((item) => item.status === 0))
const activeTasks = computed(() => tasks.value.filter((item) => item.status === 0))
const targetedTasks = computed(() => tasks.value.filter((item) => item.apprenticeId))
const submittedArtworks = computed(() => artworks.value.filter((item) => item.status === 1))
const quickApprentices = computed(() => approvedRelations.value.slice(0, 4))
const recentTasks = computed(() => tasks.value.slice(0, 5))
const pendingReviewArtworks = computed(() => artworks.value.filter((item) => item.status === 1).slice(0, 4))

function statCards() {
  return [
    { label: '已绑定徒弟', value: approvedRelations.value.length, tone: 'primary' },
    { label: '待审核申请', value: pendingRelations.value.length, tone: 'accent' },
    { label: '进行中作业', value: activeTasks.value.length, tone: 'ink' },
    { label: '定向作业', value: targetedTasks.value.length, tone: 'gold' },
    { label: '待点评作品', value: submittedArtworks.value.length, tone: 'accent' },
    { label: '已上传材料', value: materials.value.length, tone: 'primary' }
  ]
}

function statusLabel(status: number) {
  return ['进行中', '已完成', '已取消'][status] || ''
}

function relationStatusLabel(status: number) {
  return ['待审核', '已通过', '已拒绝', '已解除'][status] || ''
}
</script>

<template>
  <div class="workspace-page container">
    <section class="workspace-hero master-hero">
      <div>
        <p class="workspace-kicker">师傅工作台</p>
        <h1>今天要做的不是“看数据”，而是持续带徒、布置任务、跟进作品。</h1>
        <p class="workspace-desc">
          这里把徒弟、作业、作品点评和学习材料收在一起。你可以先看待审核申请，再给指定徒弟布置作业。
        </p>
      </div>
      <div class="workspace-actions">
        <n-button type="primary" @click="router.push('/workspace/master/tasks?create=1')">新建作业</n-button>
        <n-button @click="router.push('/workspace/master/apprentices')">管理徒弟</n-button>
        <n-button @click="router.push('/workspace/master/materials?create=1')">上传材料</n-button>
      </div>
    </section>

    <section class="stats-grid">
      <div v-for="card in statCards()" :key="card.label" class="stat-card" :data-tone="card.tone">
        <span class="stat-label">{{ card.label }}</span>
        <strong class="stat-value">{{ card.value }}</strong>
      </div>
    </section>

    <section class="workspace-grid">
      <div class="panel card apprentice-panel">
        <div class="panel-head">
          <div>
            <h2>徒弟总览</h2>
            <p>快速进入某个徒弟的作业与成长跟踪。</p>
          </div>
          <n-button text @click="router.push('/workspace/master/apprentices')">查看全部</n-button>
        </div>
        <div v-if="quickApprentices.length" class="apprentice-list">
          <div v-for="relation in quickApprentices" :key="relation.id" class="apprentice-item">
            <div>
              <strong>{{ relation.apprenticeName || '未命名徒弟' }}</strong>
              <p>{{ relation.projectName || '未关联传承项目' }}</p>
            </div>
            <div class="item-actions">
              <n-tag size="small" type="success" :bordered="false">{{ relationStatusLabel(relation.status) }}</n-tag>
              <n-button size="small" @click="router.push(`/workspace/master/tasks?apprenticeId=${relation.apprenticeId}&apprenticeName=${encodeURIComponent(relation.apprenticeName || '')}`)">布置作业</n-button>
              <n-button size="small" tertiary @click="router.push(`/workspace/master/apprentices/${relation.apprenticeId}/growth?name=${encodeURIComponent(relation.apprenticeName || '')}`)">成长记录</n-button>
            </div>
          </div>
        </div>
        <n-empty v-else description="还没有已绑定徒弟" style="padding:48px 0;" />
      </div>

      <div class="panel card task-panel">
        <div class="panel-head">
          <div>
            <h2>最近作业</h2>
            <p>支持面向全部徒弟，也支持定向布置。</p>
          </div>
          <n-button text @click="router.push('/workspace/master/tasks')">进入任务中心</n-button>
        </div>
        <div v-if="recentTasks.length" class="task-list">
          <div v-for="task in recentTasks" :key="task.id" class="task-item" @click="router.push(`/workspace/master/tasks/${task.id}`)">
            <div>
              <strong>{{ task.title }}</strong>
              <p>对象：{{ task.apprenticeName || '全部徒弟' }}</p>
            </div>
            <div class="task-meta">
              <n-tag size="small" :bordered="false" :type="task.status === 0 ? 'warning' : 'success'">{{ statusLabel(task.status) }}</n-tag>
              <span v-if="task.deadline">{{ task.deadline.split('T')[0] }}</span>
            </div>
          </div>
        </div>
        <n-empty v-else description="还没有教学作业" style="padding:48px 0;" />
      </div>
    </section>

    <section class="workspace-grid lower-grid">
      <div class="panel card">
        <div class="panel-head">
          <div>
            <h2>待点评作品</h2>
            <p>徒弟已提交作品后，可以直接进入点评。</p>
          </div>
          <n-button text @click="router.push('/workspace/master/artworks')">作品中心</n-button>
        </div>
        <div v-if="pendingReviewArtworks.length" class="review-list">
          <div v-for="artwork in pendingReviewArtworks" :key="artwork.id" class="review-item">
            <div>
              <strong>{{ artwork.title }}</strong>
              <p>{{ artwork.description?.slice(0, 42) || '等待师傅点评' }}</p>
            </div>
            <n-button size="small" type="primary" @click="router.push('/workspace/master/artworks')">去点评</n-button>
          </div>
        </div>
        <n-empty v-else description="当前没有待点评作品" style="padding:48px 0;" />
      </div>

      <div class="panel card">
        <div class="panel-head">
          <div>
            <h2>快捷操作</h2>
            <p>日常高频操作放在这里，减少来回切换。</p>
          </div>
        </div>
        <div class="quick-grid">
          <button class="quick-card" @click="router.push('/workspace/master/tasks?create=1')">
            <strong>布置新作业</strong>
            <span>支持指定徒弟或面向全部徒弟</span>
          </button>
          <button class="quick-card" @click="router.push('/workspace/master/apprentices')">
            <strong>审核徒弟申请</strong>
            <span>待审核 {{ pendingRelations.length }} 条</span>
          </button>
          <button class="quick-card" @click="router.push('/workspace/master/materials?create=1')">
            <strong>上传学习材料</strong>
            <span>把图文、PDF、视频资料发给徒弟</span>
          </button>
          <button class="quick-card" @click="router.push('/workspace/master/artworks')">
            <strong>进入点评区</strong>
            <span>集中查看徒弟作品与评分</span>
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
    linear-gradient(135deg, rgba(15, 39, 64, 0.96), rgba(41, 65, 91, 0.92)),
    radial-gradient(circle at top right, rgba(230, 184, 92, 0.25), transparent 36%);
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
  color: rgba(255,255,255,0.78);
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

.stat-card[data-tone='accent'] { background: linear-gradient(180deg, #fff5f2, #fff); }
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
  grid-template-columns: 1.25fr 1fr;
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

.apprentice-list,
.task-list,
.review-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.apprentice-item,
.review-item {
  display: flex;
  justify-content: space-between;
  gap: 14px;
  padding: 14px 16px;
  border-radius: 16px;
  background: #fbfaf7;
  border: 1px solid var(--border-light);
}

.task-item {
  display: flex;
  justify-content: space-between;
  gap: 14px;
  padding: 14px 16px;
  border-radius: 16px;
  background: #fbfaf7;
  border: 1px solid var(--border-light);
  cursor: pointer;
}

.apprentice-item strong,
.task-item strong,
.review-item strong {
  color: var(--text-primary);
  font-size: 15px;
}

.apprentice-item p,
.task-item p,
.review-item p {
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

  .apprentice-item,
  .task-item,
  .review-item {
    flex-direction: column;
  }

  .item-actions,
  .task-meta {
    justify-content: flex-start;
  }
}
</style>
