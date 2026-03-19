<template>
  <div class="p-4">
    <el-card shadow="hover">
      <template #header><div class="flex justify-between items-center"><span class="text-lg font-bold">技艺类别管理</span><el-button type="primary" @click="openDialog()"><el-icon class="mr-1"><Plus /></el-icon>新增类别</el-button></div></template>
      <el-table :data="tableData" v-loading="loading" stripe border row-key="id" :tree-props="{ children: 'children' }">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="类别名称" min-width="200" />
        <el-table-column label="父级类别" width="150">
          <template #default="{ row }">
            <span v-if="row.parentName">{{ row.parentName }}</span>
            <el-tag v-else-if="!row.parentId || row.parentId === 0" type="info" size="small">顶级类别</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)"><template #reference><el-button type="danger" link size="small">删除</el-button></template></el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑类别' : '新增类别'" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="类别名称" prop="name"><el-input v-model="formData.name" /></el-form-item>
        <el-form-item label="父级类别">
          <el-tree-select v-model="formData.parentId" :data="parentOptions" :props="{ label: 'name', value: 'id', children: 'children' }" placeholder="请选择父级类别（不选则为顶级）" clearable check-strictly />
        </el-form-item>
        <el-form-item label="排序"><el-input-number v-model="formData.sortOrder" :min="0" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="formData.description" type="textarea" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, type FormInstance } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getCategoryTree, createCategory, updateCategory, deleteCategory } from '@/api/heritage'
defineOptions({ name: 'SkillCategories' })
const loading = ref(false); const submitLoading = ref(false); const tableData = ref<any[]>([]); const dialogVisible = ref(false); const isEdit = ref(false); const editId = ref(0); const formRef = ref<FormInstance>()
const formData = reactive({ name: '', parentId: 0 as number | null, sortOrder: 0, description: '' })
const rules = { name: [{ required: true, message: '请输入类别名称', trigger: 'blur' }] }

// 构建父级选择器选项（在树形数据基础上添加顶级选项）
const parentOptions = computed(() => {
  const topOption = { id: 0, name: '无（顶级类别）', children: [] }
  return [topOption, ...tableData.value]
})

const loadData = async () => { loading.value = true; try { const res = await getCategoryTree(); tableData.value = Array.isArray(res) ? res : [] } catch(e){} finally { loading.value = false } }
const openDialog = (row?: any) => { isEdit.value = !!row; editId.value = row?.id || 0; dialogVisible.value = true; if(row) Object.assign(formData, { name: row.name, parentId: row.parentId || 0, sortOrder: row.sortOrder || 0, description: row.description || '' }); else Object.assign(formData, { name:'', parentId: 0, sortOrder: 0, description:'' }) }
const handleSubmit = async () => { if(!formRef.value) return; await formRef.value.validate(); submitLoading.value = true; try { if(isEdit.value) await updateCategory(editId.value, formData); else await createCategory(formData); ElMessage.success(isEdit.value?'更新成功':'创建成功'); dialogVisible.value = false; loadData() } catch(e){} finally { submitLoading.value = false } }
const handleDelete = async (id: number) => { try { await deleteCategory(id); ElMessage.success('删除成功'); loadData() } catch(e){} }
onMounted(() => loadData())
</script>
