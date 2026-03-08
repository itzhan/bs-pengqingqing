<template>
  <div class="card content-box">
    <div class="table-header" style="margin-bottom: 15px; display: flex; justify-content: space-between; align-items: center;">
      <div style="display: flex; gap: 10px;">
        <el-input
          v-model="queryParams.keyword"
          placeholder="请输入用户名/真实姓名/手机号"
          clearable
          style="width: 250px"
          @clear="fetchData"
        />
        <el-select
          v-model="queryParams.role"
          placeholder="请选择角色"
          clearable
          style="width: 150px"
          @change="fetchData"
        >
          <el-option label="全部" value="" />
          <el-option label="管理员" value="admin" />
          <el-option label="大师" value="master" />
          <el-option label="学徒" value="apprentice" />
          <el-option label="普通用户" value="user" />
        </el-select>
        <el-button type="primary" @click="fetchData">搜索</el-button>
      </div>
    </div>
    <el-table :data="tableData" border stripe v-loading="loading" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="realName" label="真实姓名" />
      <el-table-column prop="phone" label="手机号" />
      <el-table-column prop="role" label="角色" width="120">
        <template #default="{ row }">
          <el-tag :type="getRoleTagType(row.role)">
            {{ getRoleLabel(row.role) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button
            link
            type="primary"
            @click="handleToggleStatus(row)"
          >
            {{ row.status === 1 ? '禁用' : '启用' }}
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { getUserList, updateUserStatus } from "@/api/modules/user";

defineOptions({ name: "UserList" });

const loading = ref(false);
const tableData = ref<any[]>([]);
const total = ref(0);
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: "",
  role: ""
});

const getRoleLabel = (role: string) => {
  const roleMap: Record<string, string> = {
    admin: "管理员",
    master: "大师",
    apprentice: "学徒",
    user: "普通用户"
  };
  return roleMap[role] || role;
};

const getRoleTagType = (role: string) => {
  const typeMap: Record<string, string> = {
    admin: "danger",
    master: "warning",
    apprentice: "success",
    user: "info"
  };
  return typeMap[role] || "info";
};

const fetchData = async () => {
  loading.value = true;
  try {
    const res = await getUserList(queryParams);
    // Handle both list and records formats
    if (res.data) {
      tableData.value = res.data.list || res.data.records || [];
      total.value = res.data.total || 0;
    }
  } catch (error) {
    console.error("获取用户列表失败:", error);
  } finally {
    loading.value = false;
  }
};

const handleToggleStatus = async (row: any) => {
  try {
    await ElMessageBox.confirm(
      `确定要${row.status === 1 ? '禁用' : '启用'}用户 "${row.username}" 吗？`,
      "提示",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }
    );
    
    await updateUserStatus({
      id: row.id,
      status: row.status === 1 ? 0 : 1
    });
    
    ElMessage.success("操作成功");
    fetchData();
  } catch (error: any) {
    if (error !== "cancel") {
      console.error("操作失败:", error);
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
