import request from '@/utils/http'

/**
 * 登录
 */
export function fetchLogin(params: { username: string; password: string }) {
  return request.post<{ token: string; user: any }>({
    url: '/api/auth/login',
    params
  })
}

/**
 * 获取用户信息
 */
export function fetchGetUserInfo() {
  return request.get<any>({
    url: '/api/auth/info'
  })
}
