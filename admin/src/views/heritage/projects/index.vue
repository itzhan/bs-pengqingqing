<template>
  <div class="p-4">
    <el-card shadow="hover">
      <template #header>
        <div class="flex justify-between items-center">
          <span class="text-lg font-bold">非遗项目管理</span>
          <el-button type="primary" @click="openDialog()"><el-icon class="mr-1"><Plus /></el-icon>新增项目</el-button>
        </div>
      </template>
      <el-form :inline="true" :model="searchForm" class="mb-4">
        <el-form-item label="项目名称"><el-input v-model="searchForm.keyword" placeholder="请输入" clearable /></el-form-item>
        <el-form-item label="保护级别">
          <el-select v-model="searchForm.level" placeholder="全部" clearable>
            <el-option label="国家级" value="国家级" /><el-option label="省级" value="省级" /><el-option label="市级" value="市级" />
          </el-select>
        </el-form-item>
        <el-form-item><el-button type="primary" @click="loadData">搜索</el-button><el-button @click="resetSearch">重置</el-button></el-form-item>
      </el-form>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="项目名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="类别" width="140">
          <template #default="{ row }">{{ row.categoryName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="level" label="保护级别" width="100">
          <template #default="{ row }"><el-tag :type="row.level==='国家级'?'danger':row.level==='省级'?'warning':'info'" size="small">{{ row.level }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="region" label="地区" width="120" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }"><el-tag :type="row.status===1?'success':'danger'" size="small">{{ row.status===1?'正常':'禁用' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="170" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)"><template #reference><el-button type="danger" link size="small">删除</el-button></template></el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination class="mt-4 justify-end" v-model:current-page="page" v-model:page-size="pageSize" :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next" @change="loadData" />
    </el-card>
      <el-dialog v-model="dialogVisible" :title="isEdit?'编辑项目':'新增项目'" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="项目名称" prop="name"><el-input v-model="formData.name" /></el-form-item>
        <el-form-item label="类别" prop="categoryId">
          <el-select v-model="formData.categoryId" placeholder="请选择类别">
            <el-option
              v-for="item in categoryOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="保护级别" prop="level"><el-select v-model="formData.level"><el-option label="国家级" value="国家级" /><el-option label="省级" value="省级" /><el-option label="市级" value="市级" /></el-select></el-form-item>
        <el-form-item label="地区" prop="region"><el-input v-model="formData.region" /></el-form-item>
        <el-form-item label="简介" prop="description"><el-input v-model="formData.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="图片URL"><el-input v-model="formData.imageUrl" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, type FormInstance } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import {
  getProjectList,
  createProject,
  updateProject,
  deleteProject,
  getCategoryList
} from '@/api/heritage'
defineOptions({ name: 'HeritageProjects' })
const loading = ref(false); const submitLoading = ref(false); const tableData = ref<any[]>([]); const page = ref(1); const pageSize = ref(10); const total = ref(0)
const categoryOptions = ref<any[]>([])
const searchForm = reactive({ keyword: '', level: '' }); const dialogVisible = ref(false); const isEdit = ref(false); const editId = ref(0); const formRef = ref<FormInstance>()
const formData = reactive({ name: '', categoryId: 1, level: '', region: '', description: '', imageUrl: '' })
const rules = {
  name: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择类别', trigger: 'change' }],
  level: [{ required: true, message: '请选择保护级别', trigger: 'change' }]
}
const loadData = async () => { loading.value = true; try { const res = await getProjectList({ page: page.value, size: pageSize.value, ...searchForm }); tableData.value = res?.records || (Array.isArray(res) ? res : []); total.value = res?.total || tableData.value.length } catch(e){console.error(e)} finally { loading.value = false } }
const loadCategories = async () => {
  try {
    const res = await getCategoryList()
    categoryOptions.value = Array.isArray(res) ? res : []
    if (!categoryOptions.value.some((item) => item.id === formData.categoryId)) {
      formData.categoryId = categoryOptions.value[0]?.id || 1
    }
  } catch (e) {}
}
const resetSearch = () => { searchForm.keyword = ''; searchForm.level = ''; page.value = 1; loadData() }
const openDialog = (row?: any) => { isEdit.value = !!row; editId.value = row?.id || 0; dialogVisible.value = true; if(row) Object.assign(formData, { name:row.name, categoryId:row.categoryId, level:row.level, region:row.region, description:row.description, imageUrl:row.imageUrl }); else Object.assign(formData, { name:'', categoryId:1, level:'', region:'', description:'', imageUrl:'' }) }
const handleSubmit = async () => { if(!formRef.value) return; await formRef.value.validate(); submitLoading.value = true; try { if(isEdit.value) await updateProject(editId.value, formData); else await createProject(formData); ElMessage.success(isEdit.value?'更新成功':'创建成功'); dialogVisible.value = false; loadData() } catch(e){} finally { submitLoading.value = false } }
const handleDelete = async (id: number) => { try { await deleteProject(id); ElMessage.success('删除成功'); loadData() } catch(e){} }
onMounted(async () => { await Promise.all([loadCategories(), loadData()]) })
</script>
