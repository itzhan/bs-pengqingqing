import { PORT1 } from "@/api/config/servicePort";
import http from "@/api";

/**
 * @name 统计模块
 */

// 获取概览统计
export const getOverviewStatistics = () => {
  return http.get(PORT1 + `/statistics/overview`);
};

// 获取师徒关系统计
export const getMasterApprenticeStatistics = () => {
  return http.get(PORT1 + `/statistics/master-apprentice`);
};

// 获取分类作品统计
export const getCategoryArtworkStatistics = () => {
  return http.get(PORT1 + `/statistics/category-artwork`);
};

// 获取月度增长统计
export const getMonthlyGrowthStatistics = () => {
  return http.get(PORT1 + `/statistics/monthly-growth`);
};

// 获取活动参与统计
export const getActivityParticipationStatistics = () => {
  return http.get(PORT1 + `/statistics/activity-participation`);
};
