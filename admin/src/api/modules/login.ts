import { Login } from "@/api/interface/index";
import authMenuList from "@/assets/json/authMenuList.json";
import http from "@/api";

// 用户登录
export const loginApi = (params: Login.ReqLoginForm) => {
  return http.post<Login.ResLogin>(`/api/auth/login`, params, { loading: false });
};

// 获取菜单列表 - 使用本地JSON数据
export const getAuthMenuListApi = () => {
  // 使用本地菜单数据
  return authMenuList;
};

// 获取按钮权限 - 返回空对象
export const getAuthButtonListApi = () => {
  return { data: {} };
};

// 用户退出登录
export const logoutApi = () => {
  return Promise.resolve();
};
