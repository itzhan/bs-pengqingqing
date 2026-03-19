<template>
  <div class="p-4">
    <el-card shadow="hover">
      <template #header><div class="flex justify-between items-center"><span class="text-lg font-bold">公告管理</span><el-button type="primary" @click="openDialog()"><el-icon class="mr-1"><Plus /></el-icon>发布公告</el-button></div></template>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="公告标题" min-width="250" show-overflow-tooltip />
        <el-table-column prop="isTop" label="置顶" width="70"><template #default="{ row }"><el-tag v-if="row.isTop===1" type="danger" size="small">置顶</el-tag><span v-else>-</span></template></el-table-column>
        <el-table-column prop="status" label="状态" width="80"><template #default="{ row }"><el-tag :type="row.status===1?'success':'info'" size="small">{{ row.status===1?'已发布':'草稿' }}</el-tag></template></el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="170" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)"><template #reference><el-button type="danger" link size="small">删除</el-button></template></el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination class="mt-4 justify-end" v-model:current-page="page" v-model:page-size="pageSize" :total="total" :page-sizes="[10,20]" layout="total,sizes,prev,pager,next" @change="loadData" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit?'编辑公告':'发布公告'" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="公告标题" prop="title"><el-input v-model="formData.title" /></el-form-item>
        <el-form-item label="公告内容" prop="content"><el-input v-model="formData.content" type="textarea" :rows="5" /></el-form-item>
        <el-form-item label="状态"><el-select v-model="formData.status"><el-option label="草稿" :value="0" /><el-option label="已发布" :value="1" /></el-select></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, type FormInstance } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getAnnouncementList, createAnnouncement, updateAnnouncement, deleteAnnouncement } from '@/api/announcement'
defineOptions({ name: 'AnnouncementList' })
const loading = ref(false); const submitLoading = ref(false); const tableData = ref<any[]>([]); const page = ref(1); const pageSize = ref(10); const total = ref(0)
const dialogVisible = ref(false); const isEdit = ref(false); const editId = ref(0); const formRef = ref<FormInstance>()
const formData = reactive({ title: '', content: '', status: 1 })
const rules = { title: [{ required: true, message: '请输入公告标题', trigger: 'blur' }], content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }] }
const loadData = async () => { loading.value = true; try { const res = await getAnnouncementList({ page: page.value, size: pageSize.value }); tableData.value = res?.records || (Array.isArray(res) ? res : []); total.value = res?.total || tableData.value.length } catch(e){} finally { loading.value = false } }
const openDialog = (row?: any) => { isEdit.value = !!row; editId.value = row?.id || 0; dialogVisible.value = true; if(row) Object.assign(formData, { title:row.title, content:row.content, status:row.status }); else Object.assign(formData, { title:'', content:'', status:1 }) }
const handleSubmit = async () => { if(!formRef.value) return; await formRef.value.validate(); submitLoading.value = true; try { if(isEdit.value) await updateAnnouncement(editId.value, formData); else await createAnnouncement(formData); ElMessage.success(isEdit.value?'更新成功':'创建成功'); dialogVisible.value = false; loadData() } catch(e){} finally { submitLoading.value = false } }
const handleDelete = async (id: number) => { try { await deleteAnnouncement(id); ElMessage.success('删除成功'); loadData() } catch(e){} }
onMounted(() => loadData())
</script>
