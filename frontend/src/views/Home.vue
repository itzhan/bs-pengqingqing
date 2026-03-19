<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { NButton, NStatistic, NGrid, NGi, NIcon, NTag, NSkeleton } from 'naive-ui'
import { getOverviewStats } from '@/api/statistics'
import { getUpcomingActivities } from '@/api/activity'
import { getPublishedAnnouncements } from '@/api/announcement'
import { getAllHeritageProjects } from '@/api/heritage'
import type { OverviewStats, Activity, Announcement, HeritageProject } from '@/types'

const router = useRouter()
const stats = ref<OverviewStats | null>(null)
const activities = ref<Activity[]>([])
const announcements = ref<Announcement[]>([])
const projects = ref<HeritageProject[]>([])
const loading = ref(true)

onMounted(async () => {
  try {
    const [s, a, an, p] = await Promise.all([
      getOverviewStats().catch(() => ({ data: null })),
      getUpcomingActivities().catch(() => ({ data: [] })),
      getPublishedAnnouncements().catch(() => ({ data: [] })),
      getAllHeritageProjects().catch(() => ({ data: [] })),
    ])
    stats.value = s.data
    activities.value = (a.data || []).slice(0, 3)
    announcements.value = (an.data || []).slice(0, 5)
    projects.value = (p.data || []).slice(0, 6)
  } finally {
    loading.value = false
  }
})

function formatDate(d: string) {
  return d ? new Date(d).toLocaleDateString('zh-CN') : ''
}
</script>

<template>
  <div class="home">
    <!-- Hero 区域 -->
    <section class="hero">
      <div class="hero-bg"></div>
      <div class="container hero-content">
        <h1 class="hero-title">传承千年技艺<br /><span>守护文化根脉</span></h1>
        <p class="hero-desc">
          非遗技艺"师徒档案"传承管理系统 —— 为非物质文化遗产传承提供全流程数字化管理，
          记录师徒传承脉络，留存珍贵技艺档案
        </p>
        <div class="hero-actions">
          <n-button type="primary" size="large" @click="router.push('/projects')">
            探索非遗项目
          </n-button>
          <n-button size="large" quaternary @click="router.push('/masters')">
            寻找传承师傅 →
          </n-button>
        </div>
      </div>
    </section>

    <!-- 统计数据 -->
    <section class="section stats-section">
      <div class="container">
        <div class="stats-grid" v-if="stats">
          <div class="stat-item">
            <div class="stat-value">{{ stats.totalProjects }}</div>
            <div class="stat-label">非遗项目</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ stats.totalMasters }}</div>
            <div class="stat-label">传承师傅</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ stats.totalApprentices }}</div>
            <div class="stat-label">学艺徒弟</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ stats.totalRelations }}</div>
            <div class="stat-label">师徒关系</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ stats.totalTasks }}</div>
            <div class="stat-label">教学任务</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ stats.totalArtworks }}</div>
            <div class="stat-label">技艺作品</div>
          </div>
        </div>
      </div>
    </section>

    <!-- 非遗项目推荐 -->
    <section class="section">
      <div class="container">
        <h2 class="section-title">非遗项目</h2>
        <p class="section-subtitle">探索中华优秀传统文化遗产</p>
        <div class="project-grid">
          <div
            v-for="p in projects" :key="p.id"
            class="card project-card"
            @click="router.push(`/projects/${p.id}`)"
          >
            <div class="project-img">
              <img :src="p.imageUrl || '/placeholder-project.svg'" :alt="p.name" />
              <span class="project-level tag tag-accent" v-if="p.level">{{ p.level }}</span>
            </div>
            <div class="project-info">
              <h3>{{ p.name }}</h3>
              <p>{{ p.description?.slice(0, 60) }}{{ p.description?.length > 60 ? '...' : '' }}</p>
              <div class="project-meta">
                <span v-if="p.region">{{ p.region }}</span>
              </div>
            </div>
          </div>
        </div>
        <div class="section-more">
          <n-button @click="router.push('/projects')" quaternary>查看全部项目 →</n-button>
        </div>
      </div>
    </section>

    <!-- 即将开始的活动 -->
    <section class="section section-alt" v-if="activities.length">
      <div class="container">
        <h2 class="section-title">即将开始的活动</h2>
        <p class="section-subtitle">参与线下体验，感受非遗魅力</p>
        <div class="activity-grid">
          <div
            v-for="act in activities" :key="act.id"
            class="card activity-card"
            @click="router.push(`/activities/${act.id}`)"
          >
            <div class="activity-cover">
              <img :src="act.coverUrl || '/placeholder-activity.svg'" :alt="act.title" />
            </div>
            <div class="activity-body">
              <h3>{{ act.title }}</h3>
              <p class="activity-desc">{{ act.description?.slice(0, 80) }}</p>
              <div class="activity-meta">
                <span>📍 {{ act.location || '线上' }}</span>
                <span>📅 {{ formatDate(act.startTime) }}</span>
              </div>
            </div>
          </div>
        </div>
        <div class="section-more">
          <n-button @click="router.push('/activities')" quaternary>查看更多活动 →</n-button>
        </div>
      </div>
    </section>

    <!-- 公告 -->
    <section class="section" v-if="announcements.length">
      <div class="container">
        <h2 class="section-title">系统公告</h2>
        <p class="section-subtitle">重要通知与动态</p>
        <div class="announcement-list">
          <div
            v-for="a in announcements" :key="a.id"
            class="announcement-item"
            @click="router.push(`/announcements/${a.id}`)"
          >
            <span class="ann-dot" :class="{ 'ann-top': a.isTop }"></span>
            <span class="ann-title">
              <n-tag size="tiny" type="error" v-if="a.isTop" :bordered="false">置顶</n-tag>
              {{ a.title }}
            </span>
            <span class="ann-date">{{ formatDate(a.createdAt) }}</span>
          </div>
        </div>
        <div class="section-more">
          <n-button @click="router.push('/announcements')" quaternary>查看全部公告 →</n-button>
        </div>
      </div>
    </section>

    <!-- CTA -->
    <section class="section cta-section">
      <div class="container cta-content">
        <h2>加入非遗传承之旅</h2>
        <p>无论你是身怀绝技的非遗传承人，还是充满热情的手艺爱好者，这里都有你的位置</p>
        <div class="cta-actions">
          <n-button type="primary" size="large" @click="router.push('/register')">立即注册</n-button>
          <n-button size="large" ghost @click="router.push('/masters')">寻找传承师傅</n-button>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
/* ── Hero ── */
.hero {
  position: relative;
  padding: 100px 0 80px;
  overflow: hidden;
}
.hero-bg {
  position: absolute; inset: 0; z-index: 0;
  background: var(--bg-hero);
}
.hero-bg::after {
  content: '';
  position: absolute; right: -80px; top: -40px;
  width: 500px; height: 500px;
  background: radial-gradient(circle, rgba(44,62,80,0.04) 0%, transparent 70%);
  border-radius: 50%;
}
.hero-content { position: relative; z-index: 1; text-align: center; }
.hero-title {
  font-family: var(--font-serif);
  font-size: 48px; font-weight: 700;
  color: var(--text-primary);
  line-height: 1.3;
  margin-bottom: 20px;
}
.hero-title span { color: var(--primary); }
.hero-desc {
  max-width: 600px; margin: 0 auto 32px;
  font-size: 16px; color: var(--text-secondary); line-height: 1.8;
}
.hero-actions { display: flex; gap: 12px; justify-content: center; }

/* ── Stats ── */
.stats-section { padding-top: 0; margin-top: -32px; position: relative; z-index: 2; }
.stats-grid {
  display: grid; grid-template-columns: repeat(6, 1fr); gap: 16px;
  background: var(--bg-card);
  border: 1px solid var(--border-light);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-md);
  padding: 32px;
}
.stat-item { text-align: center; }
.stat-value {
  font-family: var(--font-serif);
  font-size: 32px; font-weight: 700;
  color: var(--primary);
}
.stat-label {
  font-size: 13px; color: var(--text-secondary); margin-top: 4px;
}

/* ── Projects ── */
.project-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 24px; }
.project-card { cursor: pointer; }
.project-img {
  position: relative; height: 200px; overflow: hidden;
  background: var(--border-light);
}
.project-img img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.4s var(--ease); }
.project-card:hover .project-img img { transform: scale(1.05); }
.project-level { position: absolute; top: 12px; right: 12px; }
.project-info { padding: 16px; }
.project-info h3 {
  font-family: var(--font-serif);
  font-size: 16px; font-weight: 600;
  color: var(--text-primary); margin-bottom: 6px;
}
.project-info p {
  font-size: 13px; color: var(--text-secondary); line-height: 1.6;
  margin-bottom: 8px;
}
.project-meta { font-size: 12px; color: var(--text-secondary); }

/* ── Activities ── */
.section-alt { background: rgba(44,62,80,0.02); }
.activity-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 24px; }
.activity-card { cursor: pointer; }
.activity-cover { height: 180px; overflow: hidden; background: var(--border-light); }
.activity-cover img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.4s var(--ease); }
.activity-card:hover .activity-cover img { transform: scale(1.05); }
.activity-body { padding: 16px; }
.activity-body h3 {
  font-family: var(--font-serif);
  font-size: 16px; font-weight: 600;
  color: var(--text-primary); margin-bottom: 6px;
}
.activity-desc { font-size: 13px; color: var(--text-secondary); margin-bottom: 10px; }
.activity-meta {
  display: flex; gap: 16px;
  font-size: 12px; color: var(--text-secondary);
}

/* ── Announcements ── */
.announcement-list { max-width: 800px; margin: 0 auto; }
.announcement-item {
  display: flex; align-items: center; gap: 12px;
  padding: 14px 0;
  border-bottom: 1px solid var(--border-light);
  cursor: pointer;
  transition: color 0.2s;
}
.announcement-item:hover { color: var(--primary); }
.ann-dot {
  width: 6px; height: 6px; border-radius: 50%;
  background: var(--border); flex-shrink: 0;
}
.ann-dot.ann-top { background: var(--accent); }
.ann-title { flex: 1; font-size: 15px; display: flex; align-items: center; gap: 8px; }
.ann-date { font-size: 13px; color: var(--text-secondary); flex-shrink: 0; }

/* ── Section More ── */
.section-more { text-align: center; margin-top: 32px; }

/* ── CTA ── */
.cta-section {
  background: var(--primary-dark);
  margin-top: 0;
}
.cta-content { text-align: center; padding: 32px 0; }
.cta-content h2 {
  font-family: var(--font-serif);
  color: #fff; font-size: 32px; margin-bottom: 12px;
}
.cta-content p { color: rgba(255,255,255,0.7); font-size: 15px; margin-bottom: 28px; }
.cta-actions { display: flex; gap: 12px; justify-content: center; }

@media (max-width: 768px) {
  .hero-title { font-size: 32px; }
  .stats-grid { grid-template-columns: repeat(3, 1fr); }
  .project-grid, .activity-grid { grid-template-columns: 1fr; }
}
</style>
