<template>
  <div class="card content-box">
    <div class="table-header" style="margin-bottom: 15px; display: flex; justify-content: space-between; align-items: center;">
      <div style="display: flex; gap: 10px;">
        <el-input
          v-model="queryParams.keyword"
          placeholder="请输入大师姓名/称号"
          clearable
          style="width: 250px"
          @clear="fetchData"
        />
        <el-select
          v-model="queryParams.auditStatus"
          placeholder="请选择审核状态"
          clearable
          style="width: 150px"
          @change="fetchData"
        >
          <el-option label="全部" value="" />
          <el-option label="待审核" :value="0" />
          <el-option label="已通过" :value="1" />
          <el-option label="已拒绝" :value="2" />
        </el-select>
        <el-button type="primary" @click="fetchData">搜索</el-button>
      </div>
    </div>
    <el-table :data="tableData" border stripe v-loading="loading" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="masterName" label="大师姓名" />
      <el-table-column prop="title" label="称号" />
      <el-table-column prop="heritageProjectName" label="非遗项目" />
      <el-table-column prop="careerYears" label="从业年限" width="120" />
      <el-table-column prop="auditStatus" label="审核状态" width="120">
        <template #default="{ row }">
          <el-tag :type="getAuditStatusTagType(row.auditStatus)">
            {{ getAuditStatusLabel(row.auditStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="250" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleView(row)">查看</el-button>
          <el-button
            v-if="row.auditStatus === 0"
            link
            type="success"
            @click="handleAudit(row, 1)"
          >
            通过
          </el-button>
          <el-button
            v-if="row.auditStatus === 0"
            link
            type="danger"
            @click="handleAudit(row, 2)"
          >
            拒绝
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

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="大师详情" width="800px">
      <el-descriptions :column="2" border v-if="detailData">
        <el-descriptions-item label="大师姓名">{{ detailData.masterName }}</el-descriptions-item>
        <el-descriptions-item label="称号">{{ detailData.title }}</el-descriptions-item>
        <el-descriptions-item label="非遗项目">{{ detailData.heritageProjectName }}</el-descriptions-item>
        <el-descriptions-item label="从业年限">{{ detailData.careerYears }}年</el-descriptions-item>
        <el-descriptions-item label="审核状态">
          <el-tag :type="getAuditStatusTagType(detailData.auditStatus)">
            {{ getAuditStatusLabel(detailData.auditStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="手机号">{{ detailData.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ detailData.email }}</el-descriptions-item>
        <el-descriptions-item label="地址">{{ detailData.address }}</el-descriptions-item>
        <el-descriptions-item label="个人简介" :span="2">
          {{ detailData.biography }}
        </el-descriptions-item>
        <el-descriptions-item label="荣誉成就" :span="2">
          {{ detailData.honors }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 审核对话框 -->
    <el-dialog v-model="auditDialogVisible" title="审核大师" width="500px">
      <el-form :model="auditForm" label-width="100px">
        <el-form-item label="审核结果">
          <el-radio-group v-model="auditForm.status">
            <el-radio :label="1">通过</el-radio>
            <el-radio :label="2">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核备注">
          <el-input
            v-model="auditForm.remark"
            type="textarea"
            :rows="4"
            placeholder="请输入审核备注（可选）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="auditDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAuditSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { getMasterList, getMasterDetail, auditMaster } from "@/api/modules/master";

defineOptions({ name: "MasterProfile" });

const loading = ref(false);
const tableData = ref<any[]>([]);
const total = ref(0);
const detailVisible = ref(false);
const detailData = ref<any>(null);
const auditDialogVisible = ref(false);
const currentAuditRow = ref<any>(null);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: "",
  auditStatus: ""
});

const auditForm = reactive({
  status: 1,
  remark: ""
});

const getAuditStatusLabel = (status: number) => {
  const statusMap: Record<number, string> = {
    0: "待审核",
    1: "已通过",
    2: "已拒绝"
  };
  return statusMap[status] || "未知";
};

const getAuditStatusTagType = (status: number) => {
  const typeMap: Record<number, string> = {
    0: "warning",
    1: "success",
    2: "danger"
  };
  return typeMap[status] || "info";
};

const fetchData = async () => {
  loading.value = true;
  try {
    const res = await getMasterList(queryParams);
    if (res.data) {
      tableData.value = res.data.list || res.data.records || [];
      total.value = res.data.total || 0;
    }
  } catch (error) {
    console.error("获取大师列表失败:", error);
  } finally {
    loading.value = false;
  }
};

const handleView = async (row: any) => {
  try {
    const res = await getMasterDetail({ id: row.id });
    detailData.value = res.data;
    detailVisible.value = true;
  } catch (error) {
    console.error("获取大师详情失败:", error);
  }
};

const handleAudit = (row: any, status: number) => {
  currentAuditRow.value = row;
  auditForm.status = status;
  auditForm.remark = "";
  auditDialogVisible.value = true;
};

const handleAuditSubmit = async () => {
  if (!currentAuditRow.value) return;
  
  try {
    await auditMaster({
      id: currentAuditRow.value.id,
      status: auditForm.status,
      remark: auditForm.remark
    });
    
    ElMessage.success("审核成功");
    auditDialogVisible.value = false;
    fetchData();
  } catch (error) {
    console.error("审核失败:", error);
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
