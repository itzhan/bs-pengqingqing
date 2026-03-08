<template>
  <div class="card content-box">
    <div class="table-header" style="margin-bottom: 15px; display: flex; justify-content: space-between; align-items: center;">
      <div style="display: flex; gap: 10px;">
        <el-input
          v-model="queryParams.keyword"
          placeholder="请输入大师/学徒姓名"
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
          <el-option label="待审批" :value="0" />
          <el-option label="已通过" :value="1" />
          <el-option label="已拒绝" :value="2" />
          <el-option label="已解除" :value="3" />
        </el-select>
        <el-button type="primary" @click="fetchData">搜索</el-button>
      </div>
    </div>
    <el-table :data="tableData" border stripe v-loading="loading" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="masterName" label="大师姓名" />
      <el-table-column prop="apprenticeName" label="学徒姓名" />
      <el-table-column prop="heritageProjectName" label="非遗项目" />
      <el-table-column prop="status" label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="getStatusTagType(row.status)">
            {{ getStatusLabel(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="applyTime" label="申请时间" width="180" />
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="row.status === 0"
            link
            type="success"
            @click="handleApprove(row, 1)"
          >
            通过
          </el-button>
          <el-button
            v-if="row.status === 0"
            link
            type="danger"
            @click="handleApprove(row, 2)"
          >
            拒绝
          </el-button>
          <el-button
            v-if="row.status === 1"
            link
            type="warning"
            @click="handleDissolve(row)"
          >
            解除关系
          </el-button>
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

    <!-- 解除关系对话框 -->
    <el-dialog v-model="dissolveDialogVisible" title="解除师徒关系" width="500px">
      <el-form :model="dissolveForm" label-width="100px">
        <el-form-item label="解除原因">
          <el-input
            v-model="dissolveForm.reason"
            type="textarea"
            :rows="4"
            placeholder="请输入解除原因（可选）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dissolveDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleDissolveSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  getMasterRelationList,
  approveMasterRelation,
  dissolveMasterRelation
} from "@/api/modules/master";

defineOptions({ name: "MasterRelation" });

const loading = ref(false);
const tableData = ref<any[]>([]);
const total = ref(0);
const dissolveDialogVisible = ref(false);
const currentDissolveRow = ref<any>(null);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: "",
  status: ""
});

const dissolveForm = reactive({
  reason: ""
});

const getStatusLabel = (status: number) => {
  const statusMap: Record<number, string> = {
    0: "待审批",
    1: "已通过",
    2: "已拒绝",
    3: "已解除"
  };
  return statusMap[status] || "未知";
};

const getStatusTagType = (status: number) => {
  const typeMap: Record<number, string> = {
    0: "warning",
    1: "success",
    2: "danger",
    3: "info"
  };
  return typeMap[status] || "info";
};

const fetchData = async () => {
  loading.value = true;
  try {
    const res = await getMasterRelationList(queryParams);
    if (res.data) {
      tableData.value = res.data.list || res.data.records || [];
      total.value = res.data.total || 0;
    }
  } catch (error) {
    console.error("获取师徒关系列表失败:", error);
  } finally {
    loading.value = false;
  }
};

const handleApprove = async (row: any, status: number) => {
  try {
    await ElMessageBox.confirm(
      `确定要${status === 1 ? '通过' : '拒绝'}该师徒关系申请吗？`,
      "提示",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }
    );
    
    await approveMasterRelation({
      id: row.id,
      status: status,
      remark: ""
    });
    
    ElMessage.success("操作成功");
    fetchData();
  } catch (error: any) {
    if (error !== "cancel") {
      console.error("操作失败:", error);
    }
  }
};

const handleDissolve = (row: any) => {
  currentDissolveRow.value = row;
  dissolveForm.reason = "";
  dissolveDialogVisible.value = true;
};

const handleDissolveSubmit = async () => {
  if (!currentDissolveRow.value) return;
  
  try {
    await dissolveMasterRelation({
      id: currentDissolveRow.value.id,
      reason: dissolveForm.reason
    });
    
    ElMessage.success("解除关系成功");
    dissolveDialogVisible.value = false;
    fetchData();
  } catch (error) {
    console.error("解除关系失败:", error);
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
