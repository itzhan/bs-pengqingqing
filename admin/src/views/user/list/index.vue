<template>
  <div class="p-4">
    <el-card shadow="hover">
      <template #header><span class="text-lg font-bold">用户管理</span></template>
      <el-form :inline="true" :model="searchForm" class="mb-4">
        <el-form-item label="用户名"><el-input v-model="searchForm.username" clearable /></el-form-item>
        <el-form-item label="角色"><el-select v-model="searchForm.role" placeholder="全部" clearable><el-option label="管理员" value="ADMIN" /><el-option label="师傅" value="MASTER" /><el-option label="徒弟" value="APPRENTICE" /></el-select></el-form-item>
        <el-form-item><el-button type="primary" @click="loadData">搜索</el-button><el-button @click="searchForm.username=''; searchForm.role=''; loadData()">重置</el-button></el-form-item>
      </el-form>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip />
        <el-table-column prop="gender" label="性别" width="60"><template #default="{ row }">{{ row.gender===1?'男':row.gender===2?'女':'-' }}</template></el-table-column>
        <el-table-column prop="role" label="角色" width="90"><template #default="{ row }"><el-tag size="small" :type="row.role==='ADMIN'?'danger':row.role==='MASTER'?'warning':'success'">{{ row.role }}</el-tag></template></el-table-column>
        <el-table-column prop="status" label="状态" width="70"><template #default="{ row }"><el-tag :type="row.status===1?'success':'danger'" size="small">{{ row.status===1?'正常':'禁用' }}</el-tag></template></el-table-column>
        <el-table-column prop="createdAt" label="注册时间" width="170" />
      </el-table>
      <el-pagination class="mt-4 justify-end" v-model:current-page="page" v-model:page-size="pageSize" :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next" @change="loadData" />
    </el-card>
  </div>
</template>
<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getUserList } from '@/api/user'
defineOptions({ name: 'UserList' })
const loading = ref(false); const tableData = ref<any[]>([]); const page = ref(1); const pageSize = ref(10); const total = ref(0)
const searchForm = reactive({ username: '', role: '' })
const loadData = async () => { loading.value = true; try { const res = await getUserList({ page: page.value, size: pageSize.value, ...searchForm }); tableData.value = res?.records || (Array.isArray(res) ? res : []); total.value = res?.total || tableData.value.length } catch(e){} finally { loading.value = false } }
onMounted(() => loadData())
</script>
