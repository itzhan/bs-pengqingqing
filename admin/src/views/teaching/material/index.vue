<template>
  <div class="card content-box">
    <div class="table-header" style="margin-bottom: 15px; display: flex; justify-content: space-between; align-items: center;">
      <div style="display: flex; gap: 10px;">
        <el-input
          v-model="queryParams.keyword"
          placeholder="请输入材料标题"
          clearable
          style="width: 250px"
          @clear="fetchData"
        />
        <el-button type="primary" @click="fetchData">搜索</el-button>
      </div>
    </div>
    <el-table :data="tableData" border stripe v-loading="loading" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="材料标题" />
      <el-table-column prop="fileType" label="文件类型" width="120">
        <template #default="{ row }">
          <el-tag>{{ row.fileType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="fileSize" label="文件大小" width="120">
        <template #default="{ row }">
          {{ formatFileSize(row.fileSize) }}
        </template>
      </el-table-column>
      <el-table-column prop="uploaderName" label="上传者" />
      <el-table-column prop="taskTitle" label="关联任务" />
      <el-table-column prop="createTime" label="上传时间" width="180" />
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { getMaterialList, deleteMaterial } from "@/api/modules/teaching";

defineOptions({ name: "TeachingMaterial" });

const loading = ref(false);
const tableData = ref<any[]>([]);
const total = ref(0);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: ""
});

const formatFileSize = (bytes: number) => {
  if (!bytes) return "0 B";
  const k = 1024;
  const sizes = ["B", "KB", "MB", "GB"];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + " " + sizes[i];
};

const fetchData = async () => {
  loading.value = true;
  try {
    const res = await getMaterialList(queryParams);
    if (res.data) {
      tableData.value = res.data.list || res.data.records || [];
      total.value = res.data.total || 0;
    }
  } catch (error) {
    console.error("获取学习材料列表失败:", error);
  } finally {
    loading.value = false;
  }
};

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm(`确定要删除材料 "${row.title}" 吗？`, "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning"
    });
    
    await deleteMaterial({ id: [row.id] });
    ElMessage.success("删除成功");
    fetchData();
  } catch (error: any) {
    if (error !== "cancel") {
      console.error("删除失败:", error);
    }
  }
};

onMounted(() => {
  fetchData();
});
</script>

<style scoped lang="scss">
.content-box {
  padding: 20px;
}
</style>
