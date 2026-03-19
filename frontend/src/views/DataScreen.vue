<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { PieChart, BarChart, LineChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent, GridComponent } from 'echarts/components'
import {
  getOverviewStats, getMasterApprenticeStats, getCategoryArtworkStats,
  getMonthlyGrowthStats, getActivityParticipationStats
} from '@/api/statistics'
import type { OverviewStats } from '@/types'

use([CanvasRenderer, PieChart, BarChart, LineChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent])

const router = useRouter()
const stats = ref<OverviewStats | null>(null)
const categoryOption = ref({})
const masterOption = ref({})
const monthlyOption = ref({})
const activityOption = ref({})

onMounted(async () => {
  const [s, c, m, g, a] = await Promise.all([
    getOverviewStats().catch(() => ({ data: null })),
    getCategoryArtworkStats().catch(() => ({ data: [] })),
    getMasterApprenticeStats().catch(() => ({ data: [] })),
    getMonthlyGrowthStats().catch(() => ({ data: [] })),
    getActivityParticipationStats().catch(() => ({ data: [] })),
  ])
  stats.value = s.data

  // 类别作品分布（饼图）
  const cData = c.data || []
  categoryOption.value = {
    title: { text: '技艺类别作品分布', left: 'center', textStyle: { fontFamily: 'Noto Serif SC', fontSize: 16, color: '#1A1A1A' } },
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie', radius: ['40%', '70%'],
      data: cData.map((i: any) => ({ value: i.artworkCount, name: i.categoryName })),
      itemStyle: { borderRadius: 6 },
      color: ['#2C3E50', '#C0392B', '#27AE60', '#F39C12', '#3498DB', '#8E44AD']
    }]
  }

  // 师徒规模（柱状图）
  const mData = m.data || []
  masterOption.value = {
    title: { text: '师傅带徒规模', left: 'center', textStyle: { fontFamily: 'Noto Serif SC', fontSize: 16, color: '#1A1A1A' } },
    tooltip: {},
    xAxis: { type: 'category', data: mData.map((i: any) => i.masterName), axisLabel: { rotate: 30 } },
    yAxis: { type: 'value' },
    series: [
      { name: '徒弟数', type: 'bar', data: mData.map((i: any) => i.apprenticeCount), color: '#2C3E50', barWidth: 24, itemStyle: { borderRadius: [4,4,0,0] } },
      { name: '作品数', type: 'bar', data: mData.map((i: any) => i.totalArtworks), color: '#C0392B', barWidth: 24, itemStyle: { borderRadius: [4,4,0,0] } }
    ]
  }

  // 月度成长（折线图）
  const gData = g.data || []
  monthlyOption.value = {
    title: { text: '月度成长趋势', left: 'center', textStyle: { fontFamily: 'Noto Serif SC', fontSize: 16, color: '#1A1A1A' } },
    tooltip: { trigger: 'axis' },
    legend: { bottom: 0 },
    xAxis: { type: 'category', data: gData.map((i: any) => i.month) },
    yAxis: { type: 'value' },
    series: [
      { name: '新用户', type: 'line', data: gData.map((i: any) => i.newUsers), smooth: true, color: '#2C3E50' },
      { name: '新关系', type: 'line', data: gData.map((i: any) => i.newRelations), smooth: true, color: '#27AE60' },
      { name: '新任务', type: 'line', data: gData.map((i: any) => i.newTasks), smooth: true, color: '#F39C12' },
      { name: '新作品', type: 'line', data: gData.map((i: any) => i.newArtworks), smooth: true, color: '#C0392B' },
    ]
  }

  // 活动参与率（柱状图）
  const aData = a.data || []
  activityOption.value = {
    title: { text: '活动参与情况', left: 'center', textStyle: { fontFamily: 'Noto Serif SC', fontSize: 16, color: '#1A1A1A' } },
    tooltip: {},
    xAxis: { type: 'category', data: aData.map((i: any) => i.activityTitle?.slice(0, 8)), axisLabel: { rotate: 30 } },
    yAxis: { type: 'value', max: 100, axisLabel: { formatter: '{value}%' } },
    series: [{
      name: '参与率', type: 'bar', data: aData.map((i: any) => i.participationRate),
      color: '#27AE60', barWidth: 28, itemStyle: { borderRadius: [4,4,0,0] }
    }]
  }
})
</script>

<template>
  <div class="data-screen">
    <header class="ds-header">
      <span class="ds-back" @click="router.push('/')">← 返回首页</span>
      <h1>非遗传承数据可视化大屏</h1>
      <span></span>
    </header>

    <div class="ds-stats" v-if="stats">
      <div class="ds-stat" v-for="(item, key) in [
        {label:'总用户', value: stats.totalUsers, color:'#2C3E50'},
        {label:'传承师傅', value: stats.totalMasters, color:'#C0392B'},
        {label:'学艺徒弟', value: stats.totalApprentices, color:'#27AE60'},
        {label:'非遗项目', value: stats.totalProjects, color:'#F39C12'},
        {label:'师徒关系', value: stats.totalRelations, color:'#3498DB'},
        {label:'教学任务', value: stats.totalTasks, color:'#8E44AD'},
        {label:'技艺作品', value: stats.totalArtworks, color:'#E74C3C'},
      ]" :key="key">
        <div class="ds-stat-value" :style="{color: item.color}">{{ item.value }}</div>
        <div class="ds-stat-label">{{ item.label }}</div>
      </div>
    </div>

    <div class="ds-charts">
      <div class="ds-chart"><v-chart :option="categoryOption" autoresize style="height:360px;" /></div>
      <div class="ds-chart"><v-chart :option="masterOption" autoresize style="height:360px;" /></div>
      <div class="ds-chart ds-chart-wide"><v-chart :option="monthlyOption" autoresize style="height:360px;" /></div>
      <div class="ds-chart"><v-chart :option="activityOption" autoresize style="height:360px;" /></div>
    </div>
  </div>
</template>

<style scoped>
.data-screen {
  min-height: 100vh;
  background: #0D1117;
  color: #E6EDF3;
  padding: 24px;
}
.ds-header {
  display: flex; justify-content: space-between; align-items: center;
  margin-bottom: 28px; padding: 0 8px;
}
.ds-header h1 { font-family: var(--font-serif); font-size: 24px; }
.ds-back { cursor: pointer; font-size: 14px; color: rgba(255,255,255,0.6); transition: color 0.2s; }
.ds-back:hover { color: #fff; }

.ds-stats {
  display: grid; grid-template-columns: repeat(7, 1fr); gap: 16px;
  margin-bottom: 28px;
}
.ds-stat {
  background: rgba(255,255,255,0.04);
  border: 1px solid rgba(255,255,255,0.06);
  border-radius: 10px; padding: 18px; text-align: center;
}
.ds-stat-value { font-size: 32px; font-weight: 700; font-family: var(--font-serif); }
.ds-stat-label { font-size: 12px; color: rgba(255,255,255,0.5); margin-top: 4px; }

.ds-charts {
  display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px;
}
.ds-chart {
  background: rgba(255,255,255,0.03);
  border: 1px solid rgba(255,255,255,0.06);
  border-radius: 12px; padding: 20px;
}
.ds-chart-wide { grid-column: span 2; }

@media (max-width: 768px) {
  .ds-stats { grid-template-columns: repeat(3, 1fr); }
  .ds-charts { grid-template-columns: 1fr; }
  .ds-chart-wide { grid-column: span 1; }
}
</style>
