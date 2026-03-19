import request from '@/utils/request'

export const getHeritageProjects = (params?: any) => request.get('/heritage-projects', { params })
export const getAllHeritageProjects = () => request.get('/heritage-projects/all')
export const getHeritageProject = (id: number) => request.get(`/heritage-projects/${id}`)
export const getSkillCategories = () => request.get('/skill-categories')
export const getSkillCategoryTree = () => request.get('/skill-categories/tree')
