<template>
  <div class="p-4">
    <el-card shadow="hover">
      <template #header><span class="text-lg font-bold">审计日志</span></template>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="操作用户" width="120" />
        <el-table-column prop="operation" label="操作" min-width="180" />
        <el-table-column prop="method" label="请求方法" width="200" />
        <el-table-column prop="ip" label="IP地址" width="140" />
        <el-table-column prop="resultStatus" label="状态" width="80"><template #default="{ row }"><el-tag :type="row.resultStatus===1?'success':'danger'" size="small">{{ row.resultStatus===1?'成功':'失败' }}</el-tag></template></el-table-column>
        <el-table-column prop="duration" label="耗时(ms)" width="100" />
        <el-table-column prop="createdAt" label="操作时间" width="170" />
      </el-table>
      <el-pagination class="mt-4 justify-end" v-model:current-page="page" v-model:page-size="pageSize" :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next" @change="loadData" />
    </el-card>
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getAuditLogList } from '@/api/auditLog'
defineOptions({ name: 'AuditLog' })
const loading = ref(false); const tableData = ref<any[]>([]); const page = ref(1); const pageSize = ref(10); const total = ref(0)
const loadData = async () => { loading.value = true; try { const res = await getAuditLogList({ page: page.value, size: pageSize.value }); tableData.value = res?.records || res || []; total.value = res?.total || tableData.value.length } catch(e){} finally { loading.value = false } }
onMounted(() => loadData())
</script>
