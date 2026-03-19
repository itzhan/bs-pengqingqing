<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { NButton, NTag, NSkeleton } from 'naive-ui'
import { getHeritageProject } from '@/api/heritage'
import type { HeritageProject } from '@/types'

const route = useRoute()
const router = useRouter()
const project = ref<HeritageProject | null>(null)
const loading = ref(true)

onMounted(async () => {
  try {
    const res = await getHeritageProject(Number(route.params.id))
    project.value = res.data
  } finally { loading.value = false }
})
</script>

<template>
  <div class="container" style="padding-top:32px; padding-bottom:64px;">
    <n-button text @click="router.back()" style="margin-bottom:24px;">← 返回</n-button>
    <template v-if="project">
      <div class="detail-header">
        <div class="detail-img" v-if="project.imageUrl">
          <img :src="project.imageUrl" :alt="project.name" />
        </div>
        <div class="detail-info">
          <h1>{{ project.name }}</h1>
          <div class="detail-tags">
            <n-tag v-if="project.level" type="error" :bordered="false" size="small">{{ project.level }}</n-tag>
            <n-tag v-if="project.region" :bordered="false" size="small">{{ project.region }}</n-tag>
          </div>
          <p class="detail-desc">{{ project.description }}</p>
          <div class="detail-meta">
            <span>创建时间：{{ project.createdAt?.split('T')[0] }}</span>
          </div>
        </div>
      </div>
    </template>
    <n-skeleton v-else-if="loading" text :repeat="6" />
  </div>
</template>

<style scoped>
.detail-header { display: flex; gap: 32px; }
.detail-img { flex-shrink: 0; width: 400px; height: 280px; border-radius: var(--radius-lg); overflow: hidden; background: var(--border-light); }
.detail-img img { width: 100%; height: 100%; object-fit: cover; }
.detail-info { flex: 1; }
.detail-info h1 { font-family: var(--font-serif); font-size: 28px; margin-bottom: 12px; }
.detail-tags { display: flex; gap: 8px; margin-bottom: 16px; }
.detail-desc { font-size: 15px; line-height: 1.8; color: var(--text-body); margin-bottom: 16px; white-space: pre-wrap; }
.detail-meta { font-size: 13px; color: var(--text-secondary); }
@media (max-width: 768px) { .detail-header { flex-direction: column; } .detail-img { width: 100%; } }
</style>
