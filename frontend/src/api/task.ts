import request from '@/utils/request'

export const createTask = (data: any) => request.post('/tasks', data)
export const updateTask = (id: number, data: any) => request.put(`/tasks/${id}`, data)
export const updateTaskStatus = (id: number, status: number) => request.put(`/tasks/${id}/status`, null, { params: { status } })
export const getMasterTasks = (params?: any) => request.get('/tasks/master', { params })
export const getApprenticeTasks = (params?: any) => request.get('/tasks/apprentice', { params })
export const getTaskDetail = (id: number) => request.get(`/tasks/${id}`)

export const submitTask = (data: any) => request.post('/submissions', data)
export const getTaskSubmissions = (taskId: number) => request.get(`/submissions/task/${taskId}`)
export const getMySubmission = (taskId: number) => request.get(`/submissions/my/${taskId}`)
export const reviewSubmission = (id: number, data: any) => request.put(`/submissions/${id}/review`, data)
