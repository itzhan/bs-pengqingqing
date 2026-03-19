import request from '@/utils/http'

export const getTaskList = (params: any) => request.get({ url: '/api/tasks/', params })
export const getTaskDetail = (id: number) => request.get({ url: `/api/tasks/${id}` })
export const createTask = (data: any) => request.post({ url: '/api/tasks/', params: data })
export const updateTask = (id: number, data: any) => request.put({ url: `/api/tasks/${id}`, params: data })
export const getMaterialList = (params: any) => request.get({ url: '/api/materials/', params })
export const createMaterial = (data: any) => request.post({ url: '/api/materials/', params: data })
export const updateMaterial = (id: number, data: any) => request.put({ url: `/api/materials/${id}`, params: data })
export const deleteMaterial = (id: number) => request.del({ url: `/api/materials/${id}` })
