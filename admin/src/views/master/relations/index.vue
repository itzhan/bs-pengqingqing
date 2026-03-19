<template>
  <div class="p-4">
    <el-card shadow="hover">
      <template #header>
        <div class="flex items-center justify-between gap-4">
          <span class="text-lg font-bold">师徒关系</span>
          <div class="flex items-center gap-3">
            <el-select v-model="status" placeholder="全部状态" clearable style="width: 140px" @change="loadData">
              <el-option label="待审核" :value="0" />
              <el-option label="已通过" :value="1" />
              <el-option label="已拒绝" :value="2" />
              <el-option label="已解除" :value="3" />
            </el-select>
            <el-button @click="loadData">刷新</el-button>
          </div>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column label="师傅" min-width="150">
          <template #default="{ row }">
            <div>{{ row.masterName || '未命名师傅' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="徒弟" min-width="150">
          <template #default="{ row }">
            <div>{{ row.apprenticeName || '未命名徒弟' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="传承项目" min-width="160" show-overflow-tooltip>
          <template #default="{ row }">
            <span>{{ row.projectName || '未关联传承项目' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="applyReason" label="申请理由" min-width="240" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="tagType(row.status)" size="small">
              {{ statusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyTime" label="申请时间" width="170" />
        <el-table-column prop="approveTime" label="审核时间" width="170" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <div class="flex gap-2">
              <el-button v-if="row.status === 0" type="primary" link @click="handleApprove(row.id, true)">通过</el-button>
              <el-button v-if="row.status === 0" type="danger" link @click="handleApprove(row.id, false)">拒绝</el-button>
              <el-button v-if="row.status === 1" type="warning" link @click="handleDissolve(row.id)">解除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        class="mt-4 justify-end"
        v-model:current-page="page"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20]"
        layout="total,sizes,prev,pager,next"
        @change="loadData"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { approveRelation, dissolveRelation, getRelationList } from '@/api/master'

defineOptions({ name: 'MasterRelations' })

const loading = ref(false)
const tableData = ref<any[]>([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const status = ref<number | undefined>()

async function loadData() {
  loading.value = true
  try {
    const res = await getRelationList({
      page: page.value,
      size: pageSize.value,
      status: status.value
    })
    tableData.value = res?.records || []
    total.value = res?.total || 0
  } finally {
    loading.value = false
  }
}

async function handleApprove(id: number, approved: boolean) {
  await approveRelation(id, approved)
  ElMessage.success(approved ? '已通过申请' : '已拒绝申请')
  loadData()
}

async function handleDissolve(id: number) {
  try {
    const { value } = await ElMessageBox.prompt('请输入解除原因（选填）', '解除关系', {
      inputPlaceholder: '例如：长期未参与传承活动',
      confirmButtonText: '确认',
      cancelButtonText: '取消'
    })
    await dissolveRelation(id, value || '')
    ElMessage.success('师徒关系已解除')
    loadData()
  } catch {
    // 用户取消时无需提示
  }
}

function statusText(value: number) {
  return ['待审核', '已通过', '已拒绝', '已解除'][value] || '未知'
}

function tagType(value: number) {
  return value === 1 ? 'success' : value === 0 ? 'warning' : value === 2 ? 'danger' : 'info'
}

onMounted(() => loadData())
</script>
