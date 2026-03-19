<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { NInput, NPagination, NSelect, NEmpty } from 'naive-ui'
import { getHeritageProjects, getSkillCategoryTree } from '@/api/heritage'
import type { HeritageProject, SkillCategory } from '@/types'

const router = useRouter()
const route = useRoute()

const projects = ref<HeritageProject[]>([])
const total = ref(0)
const page = ref(1)
const keyword = ref('')
const categoryId = ref<number | null>(null)
const level = ref<string | null>(null)
const loading = ref(false)
const categories = ref<any[]>([])

const levelOptions: any[] = [
  { label: '全部级别', value: null },
  { label: '国家级', value: '国家级' },
  { label: '省级', value: '省级' },
  { label: '市级', value: '市级' },
  { label: '县级', value: '县级' },
]

onMounted(async () => {
  try {
    const res = await getSkillCategoryTree()
    categories.value = [{ label: '全部类别', value: null }, ...(res.data || []).map((c: SkillCategory) => ({ label: c.name, value: c.id }))]
  } catch {}
  fetchData()
})

async function fetchData() {
  loading.value = true
  try {
    const params: any = { page: page.value, size: 12 }
    if (keyword.value) params.keyword = keyword.value
    if (categoryId.value) params.categoryId = categoryId.value
    if (level.value) params.level = level.value
    const res = await getHeritageProjects(params)
    projects.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

watch([page], fetchData)
function handleSearch() { page.value = 1; fetchData() }
</script>

<template>
  <div>
    <div class="page-header">
      <h1>非遗项目</h1>
      <p>探索中华非物质文化遗产瑰宝</p>
    </div>
    <div class="container">
      <div class="filter-bar">
        <n-input v-model:value="keyword" placeholder="搜索项目名称" clearable @clear="handleSearch" @keyup.enter="handleSearch" style="max-width:280px;" />
        <n-select v-model:value="categoryId" :options="categories" placeholder="类别" style="width:160px;" @update:value="handleSearch" />
        <n-select v-model:value="level" :options="levelOptions" placeholder="级别" style="width:140px;" @update:value="handleSearch" />
      </div>

      <div v-if="projects.length" class="project-grid">
        <div v-for="p in projects" :key="p.id" class="card project-card" @click="router.push(`/projects/${p.id}`)">
          <div class="project-img">
            <img :src="p.imageUrl || '/placeholder-project.svg'" :alt="p.name" />
            <span class="tag tag-accent project-level" v-if="p.level">{{ p.level }}</span>
          </div>
          <div class="project-info">
            <h3>{{ p.name }}</h3>
            <p>{{ p.description?.slice(0, 60) }}{{ (p.description?.length || 0) > 60 ? '...' : '' }}</p>
            <span class="project-region" v-if="p.region">{{ p.region }}</span>
          </div>
        </div>
      </div>
      <n-empty v-else description="暂无项目数据" style="padding:80px 0;" />

      <div class="pagination-wrap" v-if="total > 12">
        <n-pagination v-model:page="page" :page-count="Math.ceil(total / 12)" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.filter-bar { display: flex; gap: 12px; margin-bottom: 32px; flex-wrap: wrap; }
.project-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 24px; }
.project-card { cursor: pointer; }
.project-img { position: relative; height: 200px; overflow: hidden; background: var(--border-light); }
.project-img img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.4s var(--ease); }
.project-card:hover .project-img img { transform: scale(1.05); }
.project-level { position: absolute; top: 12px; right: 12px; }
.project-info { padding: 16px; }
.project-info h3 { font-family: var(--font-serif); font-size: 16px; font-weight: 600; color: var(--text-primary); margin-bottom: 6px; }
.project-info p { font-size: 13px; color: var(--text-secondary); line-height: 1.6; margin-bottom: 8px; }
.project-region { font-size: 12px; color: var(--text-secondary); }
.pagination-wrap { display: flex; justify-content: center; margin-top: 40px; padding-bottom: 40px; }

@media (max-width: 768px) { .project-grid { grid-template-columns: 1fr; } }
</style>
