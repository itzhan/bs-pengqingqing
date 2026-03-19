import request from '@/utils/request'

export const applyRelation = (data: any) => request.post('/relations/apply', data)
export const approveRelation = (id: number, approved: boolean) => request.put(`/relations/${id}/approve`, null, { params: { approved } })
export const cancelRelation = (id: number) => request.put(`/relations/${id}/cancel`)
export const dissolveRelation = (id: number, reason?: string) => request.put(`/relations/${id}/dissolve`, null, { params: { reason } })
export const getMyApprentices = (params?: any) => request.get('/relations/master', { params })
export const getMyMasters = (params?: any) => request.get('/relations/apprentice', { params })
export const getRelations = (params?: any) => request.get('/relations', { params })
