<template>
  <div class="card content-box">
    <div class="table-header" style="margin-bottom: 15px; display: flex; justify-content: space-between; align-items: center;">
      <div style="display: flex; gap: 10px;">
        <el-input
          v-model="queryParams.keyword"
          placeholder="请输入分类名称"
          clearable
          style="width: 250px"
          @clear="fetchData"
        />
        <el-button type="primary" @click="fetchData">搜索</el-button>
      </div>
      <div>
        <el-button type="primary" @click="handleAdd">新增分类</el-button>
      </div>
    </div>
    <el-table :data="tableData" border stripe v-loading="loading" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="分类名称" />
      <el-table-column prop="parentName" label="父分类" />
      <el-table-column prop="sortOrder" label="排序" width="100" />
      <el-table-column prop="description" label="描述" show-overflow-tooltip />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div style="margin-top: 15px; display: flex; justify-content: flex-end;">
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchData"
        @current-change="fetchData"
      />
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="resetForm"
    >
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="父分类" prop="parentId">
          <el-select v-model="formData.parentId" placeholder="请选择父分类（可选）" clearable style="width: 100%">
            <el-option label="无（顶级分类）" :value="null" />
            <el-option
              v-for="cat in categories"
              :key="cat.id"
              :label="cat.name"
              :value="cat.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="formData.sortOrder" :min="0" placeholder="排序值，数字越小越靠前" style="width: 100%" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入分类描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import type { FormInstance, FormRules } from "element-plus";
import {
  getCategoryList,
  addCategory,
  editCategory,
  deleteCategory,
  getCategoryTree
} from "@/api/modules/heritage";

defineOptions({ name: "HeritageCategory" });

const loading = ref(false);
const tableData = ref<any[]>([]);
const total = ref(0);
const dialogVisible = ref(false);
const dialogTitle = ref("新增分类");
const formRef = ref<FormInstance>();
const categories = ref<any[]>([]);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: ""
});

const formData = reactive({
  id: "",
  name: "",
  parentId: null as string | null,
  sortOrder: 0,
  description: ""
});

const formRules: FormRules = {
  name: [{ required: true, message: "请输入分类名称", trigger: "blur" }]
};

const fetchData = async () => {
  loading.value = true;
  try {
    const res = await getCategoryList(queryParams);
    if (res.data) {
      tableData.value = res.data.list || res.data.records || [];
      total.value = res.data.total || 0;
    }
  } catch (error) {
    console.error("获取分类列表失败:", error);
  } finally {
    loading.value = false;
  }
};

const loadCategories = async () => {
  try {
    const res = await getCategoryTree();
    categories.value = Array.isArray(res.data) ? res.data : [];
  } catch (error) {
    console.error("加载分类树失败:", error);
  }
};

const handleAdd = () => {
  dialogTitle.value = "新增分类";
  resetForm();
  dialogVisible.value = true;
};

const handleEdit = (row: any) => {
  dialogTitle.value = "编辑分类";
  Object.assign(formData, {
    id: row.id,
    name: row.name,
    parentId: row.parentId || null,
    sortOrder: row.sortOrder || 0,
    description: row.description || ""
  });
  dialogVisible.value = true;
};

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm(`确定要删除分类 "${row.name}" 吗？`, "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning"
    });
    
    await deleteCategory({ id: [row.id] });
    ElMessage.success("删除成功");
    fetchData();
    loadCategories();
  } catch (error: any) {
    if (error !== "cancel") {
      console.error("删除失败:", error);
    }
  }
};

const resetForm = () => {
  Object.assign(formData, {
    id: "",
    name: "",
    parentId: null,
    sortOrder: 0,
    description: ""
  });
  formRef.value?.clearValidate();
};

const handleSubmit = async () => {
  if (!formRef.value) return;
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (formData.id) {
          await editCategory(formData);
          ElMessage.success("编辑成功");
        } else {
          await addCategory(formData);
          ElMessage.success("新增成功");
        }
        dialogVisible.value = false;
        fetchData();
        loadCategories();
      } catch (error) {
        console.error("操作失败:", error);
      }
    }
  });
};

onMounted(() => {
  fetchData();
  loadCategories();
});
</script>

<style scoped lang="scss">
.content-box {
  padding: 20px;
}
</style>
