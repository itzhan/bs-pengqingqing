<template>
  <div class="card content-box">
    <div class="table-header" style="margin-bottom: 15px; display: flex; justify-content: space-between; align-items: center;">
      <div style="display: flex; gap: 10px;">
        <el-input
          v-model="queryParams.keyword"
          placeholder="请输入项目名称"
          clearable
          style="width: 250px"
          @clear="fetchData"
        />
        <el-select
          v-model="queryParams.status"
          placeholder="请选择状态"
          clearable
          style="width: 150px"
          @change="fetchData"
        >
          <el-option label="全部" value="" />
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
        <el-button type="primary" @click="fetchData">搜索</el-button>
      </div>
      <div>
        <el-button type="primary" @click="handleAdd">新增项目</el-button>
      </div>
    </div>
    <el-table :data="tableData" border stripe v-loading="loading" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="项目名称" />
      <el-table-column prop="categoryName" label="分类" />
      <el-table-column prop="level" label="级别" width="120">
        <template #default="{ row }">
          <el-tag :type="getLevelTagType(row.level)">
            {{ row.level }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="region" label="地区" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
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
        <el-form-item label="项目名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入项目名称" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="formData.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option
              v-for="cat in categories"
              :key="cat.id"
              :label="cat.name"
              :value="cat.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="级别" prop="level">
          <el-input v-model="formData.level" placeholder="如：国家级、省级等" />
        </el-form-item>
        <el-form-item label="地区" prop="region">
          <el-input v-model="formData.region" placeholder="请输入地区" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入项目描述"
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
  getHeritageProjectList,
  addHeritageProject,
  editHeritageProject,
  deleteHeritageProject,
  getCategoryTree
} from "@/api/modules/heritage";

defineOptions({ name: "HeritageProject" });

const loading = ref(false);
const tableData = ref<any[]>([]);
const total = ref(0);
const dialogVisible = ref(false);
const dialogTitle = ref("新增项目");
const formRef = ref<FormInstance>();
const categories = ref<any[]>([]);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: "",
  status: ""
});

const formData = reactive({
  id: "",
  name: "",
  categoryId: "",
  level: "",
  region: "",
  status: 1,
  description: ""
});

const formRules: FormRules = {
  name: [{ required: true, message: "请输入项目名称", trigger: "blur" }],
  categoryId: [{ required: true, message: "请选择分类", trigger: "change" }],
  level: [{ required: true, message: "请输入级别", trigger: "blur" }],
  region: [{ required: true, message: "请输入地区", trigger: "blur" }]
};

const getLevelTagType = (level: string) => {
  if (level?.includes("国家级")) return "danger";
  if (level?.includes("省级")) return "warning";
  return "info";
};

const fetchData = async () => {
  loading.value = true;
  try {
    const res = await getHeritageProjectList(queryParams);
    if (res.data) {
      tableData.value = res.data.list || res.data.records || [];
      total.value = res.data.total || 0;
    }
  } catch (error) {
    console.error("获取项目列表失败:", error);
  } finally {
    loading.value = false;
  }
};

const loadCategories = async () => {
  try {
    const res = await getCategoryTree();
    categories.value = Array.isArray(res.data) ? res.data : [];
  } catch (error) {
    console.error("加载分类失败:", error);
  }
};

const handleAdd = () => {
  dialogTitle.value = "新增项目";
  resetForm();
  dialogVisible.value = true;
};

const handleEdit = (row: any) => {
  dialogTitle.value = "编辑项目";
  Object.assign(formData, {
    id: row.id,
    name: row.name,
    categoryId: row.categoryId,
    level: row.level,
    region: row.region,
    status: row.status,
    description: row.description || ""
  });
  dialogVisible.value = true;
};

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm(`确定要删除项目 "${row.name}" 吗？`, "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning"
    });
    
    await deleteHeritageProject({ id: [row.id] });
    ElMessage.success("删除成功");
    fetchData();
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
    categoryId: "",
    level: "",
    region: "",
    status: 1,
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
          await editHeritageProject(formData);
          ElMessage.success("编辑成功");
        } else {
          await addHeritageProject(formData);
          ElMessage.success("新增成功");
        }
        dialogVisible.value = false;
        fetchData();
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
