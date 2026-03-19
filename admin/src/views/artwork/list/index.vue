<template>
  <div class="p-4">
    <el-card shadow="hover">
      <template #header><span class="text-lg font-bold">作品管理</span></template>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="作品名称" min-width="180" />
        <el-table-column prop="apprenticeName" label="创作者" min-width="180">
          <template #default="{ row }">
            <div class="flex items-center gap-3">
              <el-avatar v-if="row.apprenticeAvatar" :src="row.apprenticeAvatar" :size="36" />
              <el-avatar v-else :size="36">{{ (row.apprenticeName || '?').slice(0, 1) }}</el-avatar>
              <span>{{ row.apprenticeName || '-' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="projectName" label="所属项目" min-width="240">
          <template #default="{ row }">
            <div class="flex items-center gap-3">
              <el-image
                v-if="row.projectImageUrl"
                :src="row.projectImageUrl"
                :preview-src-list="[row.projectImageUrl]"
                fit="cover"
                style="width: 64px; height: 40px; border-radius: 6px"
              />
              <div v-else class="flex items-center justify-center text-xs text-gray-400 bg-gray-100 rounded-md" style="width: 64px; height: 40px">
                暂无
              </div>
              <span>{{ row.projectName || '-' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="作品图片" width="110">
          <template #default="{ row }">
            <el-image v-if="row.firstImageUrl" :src="row.firstImageUrl" :preview-src-list="[row.firstImageUrl]" fit="cover" style="width:60px;height:60px;border-radius:4px" />
            <span v-else class="text-gray-400">暂无</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type || 'info'" size="small">{{ statusMap[row.status]?.label || row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="170" />
      </el-table>
      <el-pagination class="mt-4 justify-end" v-model:current-page="page" v-model:page-size="pageSize" :total="total" :page-sizes="[10,20]" layout="total,sizes,prev,pager,next" @change="loadData" />
    </el-card>
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getArtworkList } from '@/api/artwork'
defineOptions({ name: 'ArtworkList' })

type TagType = 'primary' | 'success' | 'warning' | 'info' | 'danger'

const statusMap: Record<number, { type: TagType; label: string }> = {
  0: { type: 'info', label: '草稿' },
  1: { type: 'warning', label: '已提交' },
  2: { type: 'success', label: '已点评' }
}

const loading = ref(false)
const tableData = ref<any[]>([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const loadData = async () => {
  loading.value = true
  try {
    const res = await getArtworkList({ page: page.value, size: pageSize.value })
    tableData.value = Array.isArray(res?.records) ? res.records : []
    total.value = Number(res?.total) || 0
  } catch (e) {
    tableData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}
onMounted(() => loadData())
</script>
