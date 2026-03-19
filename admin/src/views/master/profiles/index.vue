<template>
  <div class="p-4">
    <el-card shadow="hover">
      <template #header><span class="text-lg font-bold">师傅档案</span></template>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="realName" label="师傅姓名" width="120">
          <template #default="{ row }">{{ row.realName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="title" label="称号" min-width="120" show-overflow-tooltip />
        <el-table-column prop="projectName" label="非遗项目" min-width="140" show-overflow-tooltip>
          <template #default="{ row }">{{ row.projectName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="skillCategoryName" label="技艺类别" min-width="130" show-overflow-tooltip>
          <template #default="{ row }">{{ row.skillCategoryName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="specialties" label="擅长领域" min-width="180" show-overflow-tooltip />
        <el-table-column prop="careerYears" label="从业年限" width="90" />
        <el-table-column prop="honor" label="荣誉" min-width="150" show-overflow-tooltip />
        <el-table-column prop="auditStatus" label="认证状态" width="90">
          <template #default="{ row }"><el-tag :type="row.auditStatus===1?'success':row.auditStatus===0?'warning':'info'" size="small">{{ row.auditStatus===1?'已认证':row.auditStatus===0?'待审核':'未通过' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="170" />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.auditStatus !== 1"
              type="primary"
              link
              size="small"
              @click="handleAudit(row.id, 1)"
            >
              通过
            </el-button>
            <el-button
              v-if="row.auditStatus !== 2"
              type="danger"
              link
              size="small"
              @click="handleAudit(row.id, 2)"
            >
              驳回
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination class="mt-4 justify-end" v-model:current-page="page" v-model:page-size="pageSize" :total="total" :page-sizes="[10,20]" layout="total,sizes,prev,pager,next" @change="loadData" />
    </el-card>
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { auditMasterProfile, getMasterProfileList } from '@/api/master'
defineOptions({ name: 'MasterProfiles' })
const loading = ref(false); const tableData = ref<any[]>([]); const page = ref(1); const pageSize = ref(10); const total = ref(0)
const loadData = async () => { loading.value = true; try { const res = await getMasterProfileList({ page: page.value, size: pageSize.value }); tableData.value = res?.records || (Array.isArray(res) ? res : []); total.value = res?.total || tableData.value.length } catch(e){} finally { loading.value = false } }
const handleAudit = async (id: number, auditStatus: number) => {
  await auditMasterProfile(id, auditStatus)
  ElMessage.success(auditStatus === 1 ? '审核通过' : '已驳回')
  loadData()
}
onMounted(() => loadData())
</script>
