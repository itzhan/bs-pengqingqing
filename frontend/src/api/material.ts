import request from '@/utils/request'

export const uploadMaterial = (data: any) => request.post('/materials', data)
export const deleteMaterial = (id: number) => request.delete(`/materials/${id}`)
export const getTaskMaterials = (taskId: number, params?: any) => request.get(`/materials/task/${taskId}`, { params })
export const getMyMaterials = (params?: any) => request.get('/materials/my', { params })
