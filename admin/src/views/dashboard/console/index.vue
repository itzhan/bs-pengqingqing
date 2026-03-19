<!-- 控制台首页 - 非遗传承管理系统 -->
<template>
  <div class="p-4">
    <!-- 统计卡片 -->
    <el-row :gutter="16" class="mb-4">
      <el-col :xs="12" :sm="8" :md="4" v-for="(item, i) in statCards" :key="i">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" :style="{ background: item.color }">
            <el-icon :size="24"><component :is="item.icon" /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ item.value }}</div>
            <div class="stat-label">{{ item.label }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近活动 & 最新公告 -->
    <el-row :gutter="16" class="mb-4">
      <el-col :xs="24" :md="14">
        <el-card shadow="hover">
          <template #header><span class="font-bold">最近活动</span></template>
          <el-table :data="recentActivities" size="small" max-height="280">
            <el-table-column prop="title" label="活动名称" min-width="200" show-overflow-tooltip />
            <el-table-column prop="location" label="地点" width="120" show-overflow-tooltip />
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status===0?'warning':row.status===1?'success':'info'" size="small">
                  {{ ['未开始','进行中','已结束'][row.status] || '' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="10">
        <el-card shadow="hover">
          <template #header><span class="font-bold">最新公告</span></template>
          <div v-if="recentAnnouncements.length" class="ann-list">
            <div v-for="a in recentAnnouncements" :key="a.id" class="ann-item">
              <span class="ann-title">{{ a.title }}</span>
              <span class="ann-date">{{ formatDate(a.createdAt) }}</span>
            </div>
          </div>
          <el-empty v-else description="暂无公告" :image-size="60" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 非遗项目概览 -->
    <el-card shadow="hover">
      <template #header><span class="font-bold">非遗项目 (最新)</span></template>
      <el-table :data="recentProjects" size="small" max-height="300">
        <el-table-column prop="name" label="项目名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="level" label="保护级别" width="100">
          <template #default="{ row }">
            <el-tag :type="row.level==='国家级'?'danger':row.level==='省级'?'warning':'info'" size="small">{{ row.level }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="region" label="地区" width="120" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="类别" width="120" show-overflow-tooltip />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, markRaw } from 'vue'
import { Collection, User, Calendar, Document, Star, Connection } from '@element-plus/icons-vue'
import { getProjectList } from '@/api/heritage'
import { getActivityList } from '@/api/activity'
import { getAnnouncementList } from '@/api/announcement'
import { getStatisticsOverview } from '@/api/statistics'

defineOptions({ name: 'Console' })

const statCards = ref<any[]>([])
const recentActivities = ref<any[]>([])
const recentAnnouncements = ref<any[]>([])
const recentProjects = ref<any[]>([])

function formatDate(d: string) {
  if (!d) return ''
  return new Date(d).toLocaleDateString('zh-CN')
}

onMounted(async () => {
  try {
    const stats = await getStatisticsOverview()
    statCards.value = [
      { label: '非遗项目', value: stats?.totalProjects || 0, icon: markRaw(Collection), color: '#409EFF20' },
      { label: '传承师傅', value: stats?.totalMasters || 0, icon: markRaw(Star), color: '#E6A23C20' },
      { label: '学艺徒弟', value: stats?.totalApprentices || 0, icon: markRaw(User), color: '#67C23A20' },
      { label: '师徒关系', value: stats?.totalRelations || 0, icon: markRaw(Connection), color: '#F56C6C20' },
      { label: '教学任务', value: stats?.totalTasks || 0, icon: markRaw(Document), color: '#909399' },
      { label: '技艺作品', value: stats?.totalArtworks || 0, icon: markRaw(Calendar), color: '#E6A23C20' },
    ]
  } catch (e) {
    statCards.value = [
      { label: '非遗项目', value: '-', icon: markRaw(Collection), color: '#409EFF20' },
      { label: '传承师傅', value: '-', icon: markRaw(Star), color: '#E6A23C20' },
      { label: '学艺徒弟', value: '-', icon: markRaw(User), color: '#67C23A20' },
      { label: '师徒关系', value: '-', icon: markRaw(Connection), color: '#F56C6C20' },
      { label: '教学任务', value: '-', icon: markRaw(Document), color: '#909399' },
      { label: '技艺作品', value: '-', icon: markRaw(Calendar), color: '#E6A23C20' },
    ]
  }

  try {
    const actRes = await getActivityList({ page: 1, size: 5 })
    recentActivities.value = actRes?.records || (Array.isArray(actRes) ? actRes : [])
  } catch (e) {}

  try {
    const annRes = await getAnnouncementList({ page: 1, size: 5 })
    recentAnnouncements.value = annRes?.records || (Array.isArray(annRes) ? annRes : [])
  } catch (e) {}

  try {
    const projRes = await getProjectList({ page: 1, size: 8 })
    recentProjects.value = projRes?.records || (Array.isArray(projRes) ? projRes : [])
  } catch (e) {}
})
</script>

<style scoped>
.stat-card {
  display: flex;
  align-items: center;
  padding: 0;
}
.stat-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  width: 100%;
}
.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  color: var(--el-color-primary);
}
.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: var(--el-text-color-primary);
  line-height: 1;
}
.stat-label {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  margin-top: 4px;
}
.ann-list {
  max-height: 260px;
  overflow-y: auto;
}
.ann-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid var(--el-border-color-lighter);
  font-size: 13px;
}
.ann-item:last-child { border-bottom: none; }
.ann-title {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-right: 12px;
}
.ann-date {
  color: var(--el-text-color-secondary);
  font-size: 12px;
  flex-shrink: 0;
}
</style>
