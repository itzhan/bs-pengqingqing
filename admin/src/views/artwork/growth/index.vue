<template>
  <div class="p-4">
    <el-card shadow="hover">
      <template #header><span class="text-lg font-bold">成长记录</span></template>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="apprenticeName" label="学徒姓名" width="120">
          <template #default="{ row }">
            {{ row.apprenticeName || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="recordType" label="记录类型" width="140">
          <template #default="{ row }">
            <el-tag :type="recordTypeMap[row.recordType]?.type || 'info'" size="small">{{ recordTypeMap[row.recordType]?.label || row.recordType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="记录时间" width="170" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="showDetail(row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination class="mt-4 justify-end" v-model:current-page="page" v-model:page-size="pageSize" :total="total" :page-sizes="[10,20]" layout="total,sizes,prev,pager,next" @change="loadData" />
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="成长记录详情" width="500px" destroy-on-close>
      <el-descriptions :column="1" border v-if="currentRecord">
        <el-descriptions-item label="学徒姓名">{{ currentRecord.apprenticeName }}</el-descriptions-item>
        <el-descriptions-item label="记录类型">
          <el-tag :type="recordTypeMap[currentRecord.recordType]?.type || 'info'" size="small">{{ recordTypeMap[currentRecord.recordType]?.label || currentRecord.recordType }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="标题">{{ currentRecord.title }}</el-descriptions-item>
        <el-descriptions-item label="描述">{{ currentRecord.description || '暂无描述' }}</el-descriptions-item>
        <el-descriptions-item label="关联类型">{{ currentRecord.relatedType || '-' }}</el-descriptions-item>
        <el-descriptions-item label="记录时间">{{ currentRecord.createdAt }}</el-descriptions-item>
      </el-descriptions>
      <template #footer><el-button @click="detailVisible = false">关闭</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getAllGrowthRecords } from '@/api/artwork'
defineOptions({ name: 'GrowthRecords' })

type TagType = 'primary' | 'success' | 'warning' | 'info' | 'danger'

const recordTypeMap: Record<string, { type: TagType; label: string }> = {
  TASK_COMPLETE: { type: 'success', label: '任务完成' },
  ARTWORK_SUBMIT: { type: 'warning', label: '作品提交' },
  ARTWORK_REVIEWED: { type: 'info', label: '作品点评' },
  COURSE_JOIN: { type: 'info', label: '课程参加' },
  MILESTONE: { type: 'danger', label: '里程碑' }
}

const loading = ref(false)
const tableData = ref<any[]>([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const detailVisible = ref(false)
const currentRecord = ref<any>(null)

const loadData = async () => {
  loading.value = true
  try {
    const res = await getAllGrowthRecords({ page: page.value, size: pageSize.value })
    tableData.value = Array.isArray(res?.records) ? res.records : []
    total.value = Number(res?.total) || 0
  } catch (e) {
    tableData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}
const showDetail = (row: any) => { currentRecord.value = row; detailVisible.value = true }
onMounted(() => loadData())
</script>
