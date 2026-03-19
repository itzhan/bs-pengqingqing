import request from '@/utils/http'

export const getUserList = (params: any) => request.get({ url: '/api/users/', params })
export const getUserDetail = (id: number) => request.get({ url: `/api/users/${id}` })
export const updateUserStatus = (id: number, status: number) => request.put({ url: `/api/users/${id}/status`, params: { status } })
