<template>
  <div class="card content-box">
    <div class="table-header" style="margin-bottom: 15px; display: flex; justify-content: space-between; align-items: center;">
      <div style="display: flex; gap: 10px;">
        <el-input
          v-model="queryParams.keyword"
          placeholder="请输入公告标题"
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
          <el-option label="草稿" :value="0" />
          <el-option label="已发布" :value="1" />
        </el-select>
        <el-button type="primary" @click="fetchData">搜索</el-button>
      </div>
      <div>
        <el-button type="primary" @click="handleAdd">新增公告</el-button>
      </div>
    </div>
    <el-table :data="tableData" border stripe v-loading="loading" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="公告标题" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? '已发布' : '草稿' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="isTop" label="是否置顶" width="100">
        <template #default="{ row }">
          <el-tag :type="row.isTop ? 'warning' : 'info'">
            {{ row.isTop ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
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
      width="900px"
      @close="resetForm"
    >
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="公告标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="是否置顶" prop="isTop">
          <el-radio-group v-model="formData.isTop">
            <el-radio :label="true">是</el-radio>
            <el-radio :label="false">否</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="0">草稿</el-radio>
            <el-radio :label="1">发布</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="公告内容" prop="content">
          <div style="border: 1px solid #dcdfe6; border-radius: 4px;">
            <Toolbar
              :editor="editorRef"
              :default-config="toolbarConfig"
              style="border-bottom: 1px solid #ccc"
            />
            <Editor
              v-model="formData.content"
              :default-config="editorConfig"
              style="height: 400px; overflow-y: hidden"
              @onCreated="handleEditorCreated"
            />
          </div>
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
import { ref, reactive, onMounted, shallowRef, onBeforeUnmount } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import type { FormInstance, FormRules } from "element-plus";
import { Editor, Toolbar } from "@wangeditor/editor-for-vue";
import type { IDomEditor, IToolbarConfig, IEditorConfig } from "@wangeditor/editor";
import "@wangeditor/editor/dist/css/style.css";
import {
  getAnnouncementList,
  addAnnouncement,
  editAnnouncement,
  deleteAnnouncement
} from "@/api/modules/announcement";

defineOptions({ name: "AnnouncementList" });

const loading = ref(false);
const tableData = ref<any[]>([]);
const total = ref(0);
const dialogVisible = ref(false);
const dialogTitle = ref("新增公告");
const formRef = ref<FormInstance>();
const editorRef = shallowRef<IDomEditor>();

const toolbarConfig: Partial<IToolbarConfig> = {};
const editorConfig: Partial<IEditorConfig> = {
  placeholder: "请输入公告内容..."
};

const handleEditorCreated = (editor: IDomEditor) => {
  editorRef.value = editor;
};

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: "",
  status: ""
});

const formData = reactive({
  id: "",
  title: "",
  content: "",
  status: 0,
  isTop: false
});

const formRules: FormRules = {
  title: [{ required: true, message: "请输入公告标题", trigger: "blur" }],
  content: [{ required: true, message: "请输入公告内容", trigger: "blur" }]
};

const fetchData = async () => {
  loading.value = true;
  try {
    const res = await getAnnouncementList(queryParams);
    if (res.data) {
      tableData.value = res.data.list || res.data.records || [];
      total.value = res.data.total || 0;
    }
  } catch (error) {
    console.error("获取公告列表失败:", error);
  } finally {
    loading.value = false;
  }
};

const handleAdd = () => {
  dialogTitle.value = "新增公告";
  resetForm();
  dialogVisible.value = true;
};

const handleEdit = (row: any) => {
  dialogTitle.value = "编辑公告";
  Object.assign(formData, {
    id: row.id,
    title: row.title,
    content: row.content || "",
    status: row.status,
    isTop: row.isTop || false
  });
  dialogVisible.value = true;
};

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm(`确定要删除公告 "${row.title}" 吗？`, "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning"
    });
    
    await deleteAnnouncement({ id: [row.id] });
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
    title: "",
    content: "",
    status: 0,
    isTop: false
  });
  formRef.value?.clearValidate();
};

const handleSubmit = async () => {
  if (!formRef.value) return;
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (formData.id) {
          await editAnnouncement(formData);
          ElMessage.success("编辑成功");
        } else {
          await addAnnouncement(formData);
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
});

onBeforeUnmount(() => {
  if (editorRef.value) {
    editorRef.value.destroy();
  }
});
</script>

<style scoped lang="scss">
.content-box {
  padding: 20px;
}
</style>
