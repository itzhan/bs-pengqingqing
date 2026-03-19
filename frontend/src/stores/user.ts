import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { User } from '@/types'
import request from '@/utils/request'

function normalizeRole(role?: string) {
  return (role || '').toLowerCase() as User['role']
}

function normalizeUser(user: any): User {
  return {
    ...user,
    role: normalizeRole(user?.role)
  }
}

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const user = ref<User | null>(
    localStorage.getItem('user') ? normalizeUser(JSON.parse(localStorage.getItem('user')!)) : null
  )

  const isLoggedIn = computed(() => !!token.value)
  const isMaster = computed(() => user.value?.role === 'master')
  const isApprentice = computed(() => user.value?.role === 'apprentice')

  async function login(username: string, password: string) {
    const res = await request.post('/auth/login', { username, password })
    token.value = res.data.token
    user.value = normalizeUser(res.data.user)
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('user', JSON.stringify(user.value))
    return res.data
  }

  async function register(form: any) {
    const res = await request.post('/auth/register', form)
    return res.data
  }

  async function fetchUserInfo() {
    const res = await request.get('/auth/info')
    user.value = normalizeUser(res.data)
    localStorage.setItem('user', JSON.stringify(user.value))
    return res.data
  }

  async function updateProfile(data: Partial<User>) {
    await request.put('/users/profile', data)
    await fetchUserInfo()
  }

  async function changePassword(oldPassword: string, newPassword: string) {
    await request.put('/users/password', { oldPassword, newPassword })
  }

  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  return {
    token, user, isLoggedIn, isMaster, isApprentice,
    login, register, fetchUserInfo, updateProfile, changePassword, logout
  }
})
