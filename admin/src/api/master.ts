import request from '@/utils/http'

export const getMasterProfileList = (params: any) => request.get({ url: '/api/master-profiles/', params })
export const getMasterProfile = (userId: number) => request.get({ url: `/api/master-profiles/${userId}` })
export const auditMasterProfile = (id: number, auditStatus: number) =>
  request.put({ url: `/api/master-profiles/${id}/audit?auditStatus=${auditStatus}` })
export const getRelationList = (params: any) => request.get({ url: '/api/relations/', params })
export const approveRelation = (id: number, approved: boolean) =>
  request.put({ url: `/api/relations/${id}/approve?approved=${approved}` })
export const dissolveRelation = (id: number, reason?: string) =>
  request.put({ url: `/api/relations/${id}/dissolve?reason=${encodeURIComponent(reason || '')}` })
