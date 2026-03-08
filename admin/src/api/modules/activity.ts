import http from "@/api";

// 活动列表
export const getActivityList = (params: any) => {
  return http.get(`/api/activities`, params, { loading: false });
};

// 活动详情
export const getActivityDetail = (id: number) => {
  return http.get(`/api/activities/${id}`, {}, { loading: false });
};

// 创建活动
export const createActivity = (data: any) => {
  return http.post(`/api/activities`, data);
};

// 更新活动
export const updateActivity = (id: number, data: any) => {
  return http.put(`/api/activities/${id}`, data);
};

// 删除活动
export const deleteActivity = (id: number) => {
  return http.delete(`/api/activities/${id}`);
};

// 活动参与者
export const getParticipants = (activityId: number) => {
  return http.get(`/api/activities/${activityId}/participants`, {}, { loading: false });
};
