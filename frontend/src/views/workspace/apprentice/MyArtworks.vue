<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { NButton, NTag, NEmpty, NModal, NForm, NFormItem, NInput, useMessage } from 'naive-ui'
import { getMyArtworks, createArtwork, submitArtwork, getArtworkReviews } from '@/api/artwork'
import { uploadFile } from '@/api/file'
import type { Artwork, ArtworkReview } from '@/types'

const message = useMessage()
const route = useRoute()
const artworks = ref<Artwork[]>([])
const showCreate = ref(false)
const showReviews = ref(false)
const reviews = ref<ArtworkReview[]>([])
const form = ref({ title: '', description: '', imageUrls: '', videoUrl: '' })

onMounted(async () => {
  await fetchData()
  if (route.query.create === '1') {
    showCreate.value = true
  }
})
async function fetchData() {
  const res = await getMyArtworks({ page: 1, size: 100 })
  artworks.value = res.data?.records || []
}

async function handleUploadImg(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return
  const res = await uploadFile(file)
  form.value.imageUrls = form.value.imageUrls ? form.value.imageUrls + ',' + res.data : res.data
  message.success('图片已上传')
}

async function handleCreate() {
  if (!form.value.title) { message.warning('请输入作品标题'); return }
  try {
    await createArtwork(form.value)
    message.success('作品已创建')
    showCreate.value = false
    form.value = { title: '', description: '', imageUrls: '', videoUrl: '' }
    fetchData()
  } catch (e: any) { message.error(e.message || '失败') }
}

async function handleSubmitArtwork(id: number) {
  try { await submitArtwork(id); message.success('作品已提交'); fetchData() }
  catch (e: any) { message.error(e.message || '提交失败') }
}

async function viewReviews(id: number) {
  const res = await getArtworkReviews(id)
  reviews.value = res.data || []
  showReviews.value = true
}
</script>

<template>
  <div class="container" style="max-width:900px; padding:32px 24px 64px;">
    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:24px;">
      <h2 style="font-family:var(--font-serif);">我的作品</h2>
      <n-button type="primary" @click="showCreate = true">+ 新建作品</n-button>
    </div>
    <div v-if="artworks.length" class="art-grid">
      <div v-for="a in artworks" :key="a.id" class="card" style="padding:16px;">
        <div class="art-img" v-if="a.imageUrls">
          <img :src="a.imageUrls.split(',')[0]" :alt="a.title" />
        </div>
        <h3 style="font-family:var(--font-serif); font-size:14px; margin-bottom:4px;">{{ a.title }}</h3>
        <n-tag :type="a.status === 1 ? 'success' : 'default'" size="tiny" :bordered="false">{{ a.status === 1 ? '已提交' : '草稿' }}</n-tag>
        <div style="margin-top:8px; display:flex; gap:8px;">
          <n-button v-if="a.status === 0" size="small" type="primary" @click="handleSubmitArtwork(a.id)">提交</n-button>
          <n-button size="small" @click="viewReviews(a.id)">查看点评</n-button>
        </div>
      </div>
    </div>
    <n-empty v-else description="暂无作品" style="padding:80px 0;" />

    <n-modal v-model:show="showCreate" title="新建作品" preset="card" style="max-width:500px;">
      <n-form :model="form" label-placement="top">
        <n-form-item label="作品标题"><n-input v-model:value="form.title" /></n-form-item>
        <n-form-item label="作品描述"><n-input v-model:value="form.description" type="textarea" :rows="3" /></n-form-item>
        <n-form-item label="上传图片">
          <label style="display:inline-block; padding:6px 16px; border:1px dashed var(--border); border-radius:6px; cursor:pointer; font-size:14px; color:var(--primary);">
            {{ form.imageUrls ? `已上传 ${form.imageUrls.split(',').length} 张` : '选择图片' }}
            <input type="file" accept="image/*" @change="handleUploadImg" hidden />
          </label>
        </n-form-item>
        <n-button type="primary" block @click="handleCreate">保存草稿</n-button>
      </n-form>
    </n-modal>

    <n-modal v-model:show="showReviews" title="师傅点评" preset="card" style="max-width:500px;">
      <div v-if="reviews.length">
        <div v-for="r in reviews" :key="r.id" style="padding:12px; background:var(--primary-bg); border-radius:8px; margin-bottom:8px;">
          <p style="font-size:14px;">{{ r.content }}（{{ r.score }}分）</p>
          <span style="font-size:12px; color:var(--text-secondary);">{{ r.createdAt?.split('T')[0] }}</span>
        </div>
      </div>
      <n-empty v-else description="暂无点评" />
    </n-modal>
  </div>
</template>

<style scoped>
.art-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; }
.art-img { height: 140px; overflow: hidden; border-radius: 8px; margin-bottom: 10px; background: var(--border-light); }
.art-img img { width: 100%; height: 100%; object-fit: cover; }
</style>
