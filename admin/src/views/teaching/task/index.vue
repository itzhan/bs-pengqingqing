<template>
  <div class="card content-box">
    <div class="table-header" style="margin-bottom: 15px; display: flex; justify-content: space-between; align-items: center;">
      <div style="display: flex; gap: 10px;">
        <el-input
          v-model="queryParams.keyword"
          placeholder="请输入任务标题"
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
          <el-option label="进行中" :value="1" />
          <el-option label="已完成" :value="2" />
          <el-option label="已过期" :value="3" />
        </el-select>
        <el-button type="primary" @click="fetchData">搜索</el-button>
      </div>
    </div>
    <el-table :data="tableData" border stripe v-loading="loading" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="任务标题" />
      <el-table-column prop="masterName" label="大师" />
      <el-table-column prop="heritageProjectName" label="非遗项目" />
      <el-table-column prop="deadline" label="截止时间" width="180" />
      <el-table-column prop="status" label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="getStatusTagType(row.status)">
            {{ getStatusLabel(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleView(row)">查看详情</el-button>
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

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="任务详情" width="800px">
      <el-descriptions :column="2" border v-if="detailData">
        <el-descriptions-item label="任务标题">{{ detailData.title }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusTagType(detailData.status)">
            {{ getStatusLabel(detailData.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="大师">{{ detailData.masterName }}</el-descriptions-item>
        <el-descriptions-item label="非遗项目">{{ detailData.heritageProjectName }}</el-descriptions-item>
        <el-descriptions-item label="截止时间">{{ detailData.deadline }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="任务描述" :span="2">
          {{ detailData.description }}
        </el-descriptions-item>
        <el-descriptions-item label="任务要求" :span="2">
          {{ detailData.requirements }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { getTeachingTaskList, getTeachingTaskDetail } from "@/api/modules/teaching";

defineOptions({ name: "TeachingTask" });

const loading = ref(false);
const tableData = ref<any[]>([]);
const total = ref(0);
const detailVisible = ref(false);
const detailData = ref<any>(null);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: "",
  status: ""
});

const getStatusLabel = (status: number) => {
  const statusMap: Record<number, string> = {
    1: "进行中",
    2: "已完成",
    3: "已过期"
  };
  return statusMap[status] || "未知";
};

const getStatusTagType = (status: number) => {
  const typeMap: Record<number, string> = {
    1: "warning",
    2: "success",
    3: "danger"
  };
  return typeMap[status] || "info";
};

const fetchData = async () => {
  loading.value = true;
  try {
    const res = await getTeachingTaskList(queryParams);
    if (res.data) {
      tableData.value = res.data.list || res.data.records || [];
      total.value = res.data.total || 0;
    }
  } catch (error) {
    console.error("获取教学任务列表失败:", error);
  } finally {
    loading.value = false;
  }
};

const handleView = async (row: any) => {
  try {
    const res = await getTeachingTaskDetail({ id: row.id });
    detailData.value = res.data;
    detailVisible.value = true;
  } catch (error) {
    console.error("获取任务详情失败:", error);
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
