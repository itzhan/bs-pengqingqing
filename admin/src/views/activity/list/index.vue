<template>
  <div class="p-4">
    <el-card shadow="hover">
      <template #header><div class="flex justify-between items-center"><span class="text-lg font-bold">活动管理</span><el-button type="primary" @click="openDialog()"><el-icon class="mr-1"><Plus /></el-icon>新增活动</el-button></div></template>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="活动名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="location" label="地点" width="150" show-overflow-tooltip />
        <el-table-column prop="startTime" label="开始时间" width="170" />
        <el-table-column prop="endTime" label="结束时间" width="170" />
        <el-table-column prop="maxParticipants" label="人数上限" width="90" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }"><el-tag :type="row.status===0?'warning':row.status===1?'success':'info'" size="small">{{ row.status===0?'未开始':row.status===1?'进行中':'已结束' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)"><template #reference><el-button type="danger" link size="small">删除</el-button></template></el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination class="mt-4 justify-end" v-model:current-page="page" v-model:page-size="pageSize" :total="total" :page-sizes="[10,20]" layout="total,sizes,prev,pager,next" @change="loadData" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit?'编辑活动':'新增活动'" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="活动名称" prop="title"><el-input v-model="formData.title" /></el-form-item>
        <el-form-item label="地点"><el-input v-model="formData.location" /></el-form-item>
        <el-form-item label="活动描述"><el-input v-model="formData.description" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, type FormInstance } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getActivityList, createActivity, updateActivity, deleteActivity } from '@/api/activity'
defineOptions({ name: 'ActivityList' })
const loading = ref(false); const submitLoading = ref(false); const tableData = ref<any[]>([]); const page = ref(1); const pageSize = ref(10); const total = ref(0)
const dialogVisible = ref(false); const isEdit = ref(false); const editId = ref(0); const formRef = ref<FormInstance>()
const formData = reactive({ title: '', location: '', description: '' })
const rules = { title: [{ required: true, message: '请输入活动名称', trigger: 'blur' }] }
const loadData = async () => { loading.value = true; try { const res = await getActivityList({ page: page.value, size: pageSize.value }); tableData.value = res?.records || (Array.isArray(res) ? res : []); total.value = res?.total || tableData.value.length } catch(e){} finally { loading.value = false } }
const openDialog = (row?: any) => { isEdit.value = !!row; editId.value = row?.id || 0; dialogVisible.value = true; if(row) Object.assign(formData, { title:row.title, location:row.location, description:row.description }); else Object.assign(formData, { title:'', location:'', description:'' }) }
const handleSubmit = async () => { if(!formRef.value) return; await formRef.value.validate(); submitLoading.value = true; try { if(isEdit.value) await updateActivity(editId.value, formData); else await createActivity(formData); ElMessage.success(isEdit.value?'更新成功':'创建成功'); dialogVisible.value = false; loadData() } catch(e){} finally { submitLoading.value = false } }
const handleDelete = async (id: number) => { try { await deleteActivity(id); ElMessage.success('删除成功'); loadData() } catch(e){} }
onMounted(() => loadData())
</script>
