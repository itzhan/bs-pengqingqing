<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { NButton, NTag, NEmpty, NModal, NInput, NInputNumber, useMessage } from 'naive-ui'
import { getMasterArtworks, createArtworkReview, getArtworkReviews } from '@/api/artwork'
import type { Artwork, ArtworkReview } from '@/types'

const router = useRouter()
const message = useMessage()
const artworks = ref<Artwork[]>([])
const showReview = ref(false)
const selectedArtwork = ref<Artwork | null>(null)
const reviews = ref<ArtworkReview[]>([])
const reviewForm = ref({ content: '', score: 85 })

onMounted(fetchData)

async function fetchData() {
  const res = await getMasterArtworks({ page: 1, size: 100 })
  artworks.value = res.data?.records || []
}

async function openReview(a: Artwork) {
  selectedArtwork.value = a
  const res = await getArtworkReviews(a.id)
  reviews.value = res.data || []
  showReview.value = true
}

async function handleReview() {
  if (!selectedArtwork.value || !reviewForm.value.content) { message.warning('请输入点评内容'); return }
  try {
    await createArtworkReview({ artworkId: selectedArtwork.value.id, ...reviewForm.value })
    message.success('点评成功')
    showReview.value = false
    reviewForm.value = { content: '', score: 85 }
  } catch (e: any) { message.error(e.message || '失败') }
}
</script>

<template>
  <div class="container" style="max-width:900px; padding:32px 24px 64px;">
    <h2 style="font-family:var(--font-serif); margin-bottom:24px;">徒弟作品</h2>
    <div v-if="artworks.length" class="art-grid">
      <div v-for="a in artworks" :key="a.id" class="card art-card" style="padding:16px;">
        <div class="art-img" v-if="a.imageUrls">
          <img :src="a.imageUrls.split(',')[0]" :alt="a.title" />
        </div>
        <h3>{{ a.title }}</h3>
        <p v-if="a.description">{{ a.description.slice(0, 60) }}</p>
        <n-tag :type="a.status === 1 ? 'success' : 'default'" size="tiny" :bordered="false" style="margin-top:8px;">
          {{ a.status === 1 ? '已提交' : '草稿' }}
        </n-tag>
        <n-button size="small" @click="openReview(a)" style="margin-top:8px;">查看/点评</n-button>
      </div>
    </div>
    <n-empty v-else description="暂无作品" style="padding:80px 0;" />

    <n-modal v-model:show="showReview" :title="selectedArtwork?.title" preset="card" style="max-width:560px;">
      <div v-if="reviews.length" style="margin-bottom:16px;">
        <h4 style="margin-bottom:8px;">已有点评</h4>
        <div v-for="r in reviews" :key="r.id" style="padding:8px; background:var(--primary-bg); border-radius:6px; margin-bottom:8px;">
          <p style="font-size:13px;">{{ r.content }}（{{ r.score }}分）</p>
        </div>
      </div>
      <h4 style="margin-bottom:8px;">新增点评</h4>
      <n-input v-model:value="reviewForm.content" type="textarea" :rows="3" placeholder="输入你的点评" />
      <div style="display:flex; gap:8px; align-items:center; margin-top:12px;">
        <span style="font-size:13px;">评分：</span>
        <n-input-number v-model:value="reviewForm.score" :min="0" :max="100" style="width:120px;" />
        <n-button type="primary" @click="handleReview">提交点评</n-button>
      </div>
    </n-modal>
  </div>
</template>

<style scoped>
.art-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; }
.art-img { height: 140px; overflow: hidden; border-radius: 8px; margin-bottom: 10px; background: var(--border-light); }
.art-img img { width: 100%; height: 100%; object-fit: cover; }
.art-card h3 { font-family: var(--font-serif); font-size: 14px; }
.art-card p { font-size: 12px; color: var(--text-secondary); }
</style>
