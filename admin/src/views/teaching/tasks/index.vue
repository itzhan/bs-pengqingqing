<template>
  <div class="p-4">
    <el-card shadow="hover">
      <template #header><span class="text-lg font-bold">教学任务</span></template>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="任务标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="masterName" label="师傅" width="120">
          <template #default="{ row }">{{ row.masterName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="apprenticeName" label="徒弟" width="120">
          <template #default="{ row }">{{ row.apprenticeName || '全部徒弟' }}</template>
        </el-table-column>
        <el-table-column prop="projectName" label="非遗项目" min-width="140" show-overflow-tooltip>
          <template #default="{ row }">{{ row.projectName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="skillCategoryName" label="技艺类别" min-width="130" show-overflow-tooltip>
          <template #default="{ row }">{{ row.skillCategoryName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }"><el-tag :type="row.status===1?'success':row.status===0?'info':'warning'" size="small">{{ row.status===1?'进行中':row.status===0?'未开始':'已结束' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="deadline" label="截止时间" width="170" />
        <el-table-column prop="createdAt" label="创建时间" width="170" />
      </el-table>
      <el-pagination class="mt-4 justify-end" v-model:current-page="page" v-model:page-size="pageSize" :total="total" :page-sizes="[10,20]" layout="total,sizes,prev,pager,next" @change="loadData" />
    </el-card>
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getTaskList } from '@/api/teaching'
defineOptions({ name: 'TeachingTasks' })
const loading = ref(false); const tableData = ref<any[]>([]); const page = ref(1); const pageSize = ref(10); const total = ref(0)
const loadData = async () => { loading.value = true; try { const res = await getTaskList({ page: page.value, size: pageSize.value }); tableData.value = res?.records || (Array.isArray(res) ? res : []); total.value = res?.total || tableData.value.length } catch(e){} finally { loading.value = false } }
onMounted(() => loadData())
</script>
