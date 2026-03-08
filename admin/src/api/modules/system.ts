import http from "@/api";

// 审计日志列表
export const getAuditLogList = (params: any) => {
  return http.get(`/api/audit-logs`, params, { loading: false });
};

// 统计概览
export const getStatisticsOverview = () => {
  return http.get(`/api/statistics/overview`, {}, { loading: false });
};

// 师徒统计
export const getMasterApprenticeStats = () => {
  return http.get(`/api/statistics/master-apprentice`, {}, { loading: false });
};

// 类别作品统计
export const getCategoryArtworkStats = () => {
  return http.get(`/api/statistics/category-artwork`, {}, { loading: false });
};

// 月度成长统计
export const getMonthlyGrowthStats = () => {
  return http.get(`/api/statistics/monthly-growth`, {}, { loading: false });
};

// 活动参与统计
export const getActivityParticipationStats = () => {
  return http.get(`/api/statistics/activity-participation`, {}, { loading: false });
};
