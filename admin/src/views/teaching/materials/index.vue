<template>
  <div class="p-4">
    <el-card shadow="hover">
      <template #header>
        <div class="flex justify-between items-center">
          <span class="text-lg font-bold">学习材料</span>
          <el-button type="primary" @click="openDialog()">
            <el-icon class="mr-1"><Plus /></el-icon>新增材料
          </el-button>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="材料标题" min-width="200" />
        <el-table-column prop="fileType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.fileType" :type="(typeTagMap[row.fileType] || 'info') as any" size="small">{{ row.fileType }}</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="uploaderName" label="上传者" width="120">
          <template #default="{ row }">
            {{ row.uploaderName || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="上传时间" width="170" />
        <el-table-column label="操作" width="170">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)">
              <template #reference><el-button type="danger" link size="small">删除</el-button></template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination class="mt-4 justify-end" v-model:current-page="page" v-model:page-size="pageSize" :total="total" :page-sizes="[10,20]" layout="total,sizes,prev,pager,next" @change="loadData" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑学习材料' : '新增学习材料'" width="620px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="材料标题" prop="title">
          <el-input v-model="formData.title" />
        </el-form-item>
        <el-form-item label="文件链接" prop="fileUrl">
          <el-input v-model="formData.fileUrl" />
        </el-form-item>
        <el-form-item label="类型" prop="fileType">
          <el-select v-model="formData.fileType" placeholder="请选择类型">
            <el-option label="PDF" value="PDF" />
            <el-option label="IMAGE" value="IMAGE" />
            <el-option label="VIDEO" value="VIDEO" />
            <el-option label="DOC" value="DOC" />
          </el-select>
        </el-form-item>
        <el-form-item label="文件大小(B)" prop="fileSize">
          <el-input-number v-model="formData.fileSize" :min="0" :step="1024" />
        </el-form-item>
        <el-form-item label="关联任务">
          <el-select v-model="formData.taskId" clearable placeholder="可选">
            <el-option
              v-for="item in taskOptions"
              :key="item.id"
              :label="item.title"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="关联项目">
          <el-select v-model="formData.heritageProjectId" clearable placeholder="可选">
            <el-option
              v-for="item in projectOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="formData.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, type FormInstance } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getMaterialList, createMaterial, updateMaterial, deleteMaterial, getTaskList } from '@/api/teaching'
import { getAllProjects } from '@/api/heritage'
defineOptions({ name: 'LearningMaterials' })

const typeTagMap: Record<string, string> = { PDF: 'danger', IMAGE: 'success', VIDEO: 'warning', DOC: '' }
const loading = ref(false); const tableData = ref<any[]>([]); const page = ref(1); const pageSize = ref(10); const total = ref(0)
const dialogVisible = ref(false); const submitLoading = ref(false); const isEdit = ref(false); const editId = ref(0)
const formRef = ref<FormInstance>()
const taskOptions = ref<any[]>([])
const projectOptions = ref<any[]>([])
const formData = ref<any>({
  title: '',
  description: '',
  fileUrl: '',
  fileType: 'PDF',
  fileSize: 0,
  taskId: undefined,
  heritageProjectId: undefined
})
const rules = {
  title: [{ required: true, message: '请输入材料标题', trigger: 'blur' }],
  fileUrl: [{ required: true, message: '请输入文件链接', trigger: 'blur' }]
}
const loadData = async () => { loading.value = true; try { const res = await getMaterialList({ page: page.value, size: pageSize.value }); tableData.value = res?.records || res || []; total.value = res?.total || tableData.value.length } catch(e){} finally { loading.value = false } }
const loadOptions = async () => {
  try {
    const [taskRes, projectRes] = await Promise.all([
      getTaskList({ page: 1, size: 200 }),
      getAllProjects()
    ])
    taskOptions.value = taskRes?.records || []
    projectOptions.value = Array.isArray(projectRes) ? projectRes : []
  } catch (e) {}
}
const openDialog = (row?: any) => {
  isEdit.value = !!row
  editId.value = row?.id || 0
  dialogVisible.value = true
  if (row) {
    formData.value = {
      title: row.title || '',
      description: row.description || '',
      fileUrl: row.fileUrl || '',
      fileType: row.fileType || 'PDF',
      fileSize: row.fileSize || 0,
      taskId: row.taskId,
      heritageProjectId: row.heritageProjectId
    }
    return
  }
  formData.value = {
    title: '',
    description: '',
    fileUrl: '',
    fileType: 'PDF',
    fileSize: 0,
    taskId: undefined,
    heritageProjectId: undefined
  }
}
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  submitLoading.value = true
  try {
    if (isEdit.value) await updateMaterial(editId.value, formData.value)
    else await createMaterial(formData.value)
    ElMessage.success(isEdit.value ? '更新成功' : '新增成功')
    dialogVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}
const handleDelete = async (id: number) => { try { await deleteMaterial(id); ElMessage.success('删除成功'); loadData() } catch(e){} }
onMounted(async () => { await Promise.all([loadData(), loadOptions()]) })
</script>
