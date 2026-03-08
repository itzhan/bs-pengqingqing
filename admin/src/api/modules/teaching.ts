import http from "@/api";

// 教学任务列表
export const getTaskList = (params: any) => {
  return http.get(`/api/tasks`, params, { loading: false });
};

// 任务详情
export const getTaskDetail = (id: number) => {
  return http.get(`/api/tasks/${id}`, {}, { loading: false });
};

// 任务的提交列表
export const getTaskSubmissions = (taskId: number) => {
  return http.get(`/api/submissions/task/${taskId}`, {}, { loading: false });
};

// 学习材料列表
export const getMaterialList = (params: any) => {
  return http.get(`/api/materials`, params, { loading: false });
};

// 删除材料
export const deleteMaterial = (id: number) => {
  return http.delete(`/api/materials/${id}`);
};
