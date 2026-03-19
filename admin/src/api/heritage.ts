import request from '@/utils/http'

// 非遗项目
export const getProjectList = (params: any) => request.get({ url: '/api/heritage-projects/', params })
export const getAllProjects = () => request.get({ url: '/api/heritage-projects/all' })
export const getProjectDetail = (id: number) => request.get({ url: `/api/heritage-projects/${id}` })
export const createProject = (data: any) => request.post({ url: '/api/heritage-projects/', params: data })
export const updateProject = (id: number, data: any) => request.put({ url: `/api/heritage-projects/${id}`, params: data })
export const deleteProject = (id: number) => request.del({ url: `/api/heritage-projects/${id}` })

// 技艺类别
export const getCategoryList = () => request.get({ url: '/api/skill-categories/' })
export const getCategoryTree = () => request.get({ url: '/api/skill-categories/tree' })
export const createCategory = (data: any) => request.post({ url: '/api/skill-categories/', params: data })
export const updateCategory = (id: number, data: any) => request.put({ url: `/api/skill-categories/${id}`, params: data })
export const deleteCategory = (id: number) => request.del({ url: `/api/skill-categories/${id}` })
