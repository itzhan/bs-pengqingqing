import http from "@/api";

// 用户列表
export const getUserList = (params: any) => {
  return http.get(`/api/users`, params, { loading: false });
};

// 用户详情
export const getUserDetail = (id: number) => {
  return http.get(`/api/users/${id}`, {}, { loading: false });
};

// 更新用户状态
export const updateUserStatus = (id: number, status: number) => {
  return http.put(`/api/users/${id}/status`, { status });
};

// 获取当前用户信息
export const getUserInfo = () => {
  return http.get(`/api/auth/info`, {}, { loading: false });
};
