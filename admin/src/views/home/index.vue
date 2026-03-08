<template>
  <div class="card content-box">
    <!-- 统计卡片 -->
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="总用户数" :value="stats.totalUsers">
            <template #prefix>
              <el-icon style="vertical-align: -0.125em"><User /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="大师数量" :value="stats.totalMasters">
            <template #prefix>
              <el-icon style="vertical-align: -0.125em"><Star /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="学徒数量" :value="stats.totalApprentices">
            <template #prefix>
              <el-icon style="vertical-align: -0.125em"><UserFilled /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="非遗项目" :value="stats.totalProjects">
            <template #prefix>
              <el-icon style="vertical-align: -0.125em"><Collection /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>师徒关系分布</span>
          </template>
          <div ref="masterApprenticeChartRef" style="width: 100%; height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>分类作品分布</span>
          </template>
          <div ref="categoryArtworkChartRef" style="width: 100%; height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <span>月度增长趋势</span>
          </template>
          <div ref="monthlyGrowthChartRef" style="width: 100%; height: 350px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近公告 -->
    <el-card shadow="hover">
      <template #header>
        <span>最近公告</span>
      </template>
      <el-table :data="recentAnnouncements" stripe style="width: 100%">
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="createTime" label="发布时间" width="180" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from "vue";
import { ElMessage } from "element-plus";
import { User, Star, UserFilled, Collection } from "@element-plus/icons-vue";
import * as echarts from "echarts";
import { getOverviewStatistics, getMasterApprenticeStatistics, getCategoryArtworkStatistics, getMonthlyGrowthStatistics } from "@/api/modules/statistics";
import { getPublishedAnnouncements } from "@/api/modules/announcement";

defineOptions({ name: "Home" });

const stats = reactive({
  totalUsers: 0,
  totalMasters: 0,
  totalApprentices: 0,
  totalProjects: 0
});

const recentAnnouncements = ref<any[]>([]);
const masterApprenticeChartRef = ref<HTMLElement>();
const categoryArtworkChartRef = ref<HTMLElement>();
const monthlyGrowthChartRef = ref<HTMLElement>();

let masterApprenticeChart: echarts.ECharts | null = null;
let categoryArtworkChart: echarts.ECharts | null = null;
let monthlyGrowthChart: echarts.ECharts | null = null;

const loadOverviewStats = async () => {
  try {
    const res = await getOverviewStatistics();
    if (res.data) {
      stats.totalUsers = res.data.totalUsers || 0;
      stats.totalMasters = res.data.totalMasters || 0;
      stats.totalApprentices = res.data.totalApprentices || 0;
      stats.totalProjects = res.data.totalProjects || 0;
    }
  } catch (error) {
    console.error("加载概览统计失败:", error);
  }
};

const loadMasterApprenticeChart = async () => {
  try {
    const res = await getMasterApprenticeStatistics();
    if (!masterApprenticeChartRef.value) return;
    
    if (!masterApprenticeChart) {
      masterApprenticeChart = echarts.init(masterApprenticeChartRef.value);
    }

    const data = res.data || [];
    const option = {
      tooltip: {
        trigger: "axis",
        axisPointer: { type: "shadow" }
      },
      xAxis: {
        type: "category",
        data: data.map((item: any) => item.name || item.projectName || "")
      },
      yAxis: {
        type: "value"
      },
      series: [
        {
          name: "大师数",
          type: "bar",
          data: data.map((item: any) => item.masterCount || 0)
        },
        {
          name: "学徒数",
          type: "bar",
          data: data.map((item: any) => item.apprenticeCount || 0)
        }
      ]
    };
    masterApprenticeChart.setOption(option);
  } catch (error) {
    console.error("加载师徒关系统计失败:", error);
  }
};

const loadCategoryArtworkChart = async () => {
  try {
    const res = await getCategoryArtworkStatistics();
    if (!categoryArtworkChartRef.value) return;
    
    if (!categoryArtworkChart) {
      categoryArtworkChart = echarts.init(categoryArtworkChartRef.value);
    }

    const data = res.data || [];
    const option = {
      tooltip: {
        trigger: "item"
      },
      legend: {
        orient: "vertical",
        left: "left"
      },
      series: [
        {
          name: "作品数量",
          type: "pie",
          radius: "50%",
          data: data.map((item: any) => ({
            value: item.count || 0,
            name: item.name || item.categoryName || ""
          })),
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: "rgba(0, 0, 0, 0.5)"
            }
          }
        }
      ]
    };
    categoryArtworkChart.setOption(option);
  } catch (error) {
    console.error("加载分类作品统计失败:", error);
  }
};

const loadMonthlyGrowthChart = async () => {
  try {
    const res = await getMonthlyGrowthStatistics();
    if (!monthlyGrowthChartRef.value) return;
    
    if (!monthlyGrowthChart) {
      monthlyGrowthChart = echarts.init(monthlyGrowthChartRef.value);
    }

    const data = res.data || [];
    const option = {
      tooltip: {
        trigger: "axis"
      },
      legend: {
        data: ["用户增长", "作品增长", "活动增长"]
      },
      grid: {
        left: "3%",
        right: "4%",
        bottom: "3%",
        containLabel: true
      },
      xAxis: {
        type: "category",
        boundaryGap: false,
        data: data.map((item: any) => item.month || "")
      },
      yAxis: {
        type: "value"
      },
      series: [
        {
          name: "用户增长",
          type: "line",
          data: data.map((item: any) => item.userGrowth || 0)
        },
        {
          name: "作品增长",
          type: "line",
          data: data.map((item: any) => item.artworkGrowth || 0)
        },
        {
          name: "活动增长",
          type: "line",
          data: data.map((item: any) => item.activityGrowth || 0)
        }
      ]
    };
    monthlyGrowthChart.setOption(option);
  } catch (error) {
    console.error("加载月度增长统计失败:", error);
  }
};

const loadRecentAnnouncements = async () => {
  try {
    const res = await getPublishedAnnouncements({ limit: 5 });
    recentAnnouncements.value = Array.isArray(res.data) ? res.data : [];
  } catch (error) {
    console.error("加载最近公告失败:", error);
  }
};

const initCharts = async () => {
  await nextTick();
  await Promise.all([
    loadMasterApprenticeChart(),
    loadCategoryArtworkChart(),
    loadMonthlyGrowthChart()
  ]);
};

onMounted(async () => {
  await Promise.all([
    loadOverviewStats(),
    loadRecentAnnouncements()
  ]);
  await initCharts();
  
  // 窗口大小改变时重新调整图表
  window.addEventListener("resize", () => {
    masterApprenticeChart?.resize();
    categoryArtworkChart?.resize();
    monthlyGrowthChart?.resize();
  });
});
</script>

<style scoped lang="scss">
.content-box {
  padding: 20px;
}
</style>
