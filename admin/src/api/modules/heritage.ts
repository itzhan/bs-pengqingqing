import http from "@/api";

// 非遗项目列表
export const getProjectList = (params: any) => {
  return http.get(`/api/heritage-projects`, params, { loading: false });
};

// 所有非遗项目
export const getAllProjects = () => {
  return http.get(`/api/heritage-projects/all`, {}, { loading: false });
};

// 项目详情
export const getProjectDetail = (id: number) => {
  return http.get(`/api/heritage-projects/${id}`, {}, { loading: false });
};

// 创建项目
export const createProject = (data: any) => {
  return http.post(`/api/heritage-projects`, data);
};

// 更新项目
export const updateProject = (id: number, data: any) => {
  return http.put(`/api/heritage-projects/${id}`, data);
};

// 删除项目
export const deleteProject = (id: number) => {
  return http.delete(`/api/heritage-projects/${id}`);
};

// 技艺类别列表
export const getCategoryList = () => {
  return http.get(`/api/skill-categories`, {}, { loading: false });
};

// 技艺类别树
export const getCategoryTree = () => {
  return http.get(`/api/skill-categories/tree`, {}, { loading: false });
};

// 创建类别
export const createCategory = (data: any) => {
  return http.post(`/api/skill-categories`, data);
};

// 更新类别
export const updateCategory = (id: number, data: any) => {
  return http.put(`/api/skill-categories/${id}`, data);
};

// 删除类别
export const deleteCategory = (id: number) => {
  return http.delete(`/api/skill-categories/${id}`);
};
