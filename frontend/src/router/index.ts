import { createRouter, createWebHistory } from 'vue-router'

function normalizeRole(role?: string) {
  return (role || '').toLowerCase()
}

const router = createRouter({
  history: createWebHistory(),
  scrollBehavior: () => ({ top: 0 }),
  routes: [
    {
      path: '/',
      component: () => import('@/layouts/MainLayout.vue'),
      children: [
        { path: '', name: 'Home', component: () => import('@/views/Home.vue'), meta: { title: '首页' } },
        { path: 'projects', name: 'Projects', component: () => import('@/views/projects/ProjectList.vue'), meta: { title: '非遗项目' } },
        { path: 'projects/:id', name: 'ProjectDetail', component: () => import('@/views/projects/ProjectDetail.vue'), meta: { title: '项目详情' } },
        { path: 'masters', name: 'Masters', component: () => import('@/views/masters/MasterList.vue'), meta: { title: '传承师傅' } },
        { path: 'masters/:id', name: 'MasterDetail', component: () => import('@/views/masters/MasterDetail.vue'), meta: { title: '师傅详情' } },
        { path: 'activities', name: 'Activities', component: () => import('@/views/activities/ActivityList.vue'), meta: { title: '活动课程' } },
        { path: 'activities/:id', name: 'ActivityDetail', component: () => import('@/views/activities/ActivityDetail.vue'), meta: { title: '活动详情' } },
        { path: 'announcements', name: 'Announcements', component: () => import('@/views/announcements/AnnouncementList.vue'), meta: { title: '系统公告' } },
        { path: 'announcements/:id', name: 'AnnouncementDetail', component: () => import('@/views/announcements/AnnouncementDetail.vue'), meta: { title: '公告详情' } },
        // 个人中心
        { path: 'profile', name: 'Profile', component: () => import('@/views/profile/Profile.vue'), meta: { title: '个人中心', requiresAuth: true } },
        // 师傅工作台
        {
          path: 'workspace/master',
          meta: { requiresAuth: true, role: 'master' },
          children: [
            { path: '', redirect: '/workspace/master/dashboard' },
            { path: 'dashboard', name: 'MasterDashboard', component: () => import('@/views/workspace/master/Dashboard.vue'), meta: { title: '师傅工作台' } },
            { path: 'profile', name: 'MasterWorkProfile', component: () => import('@/views/workspace/master/MasterProfileEdit.vue'), meta: { title: '我的档案' } },
            { path: 'apprentices', name: 'MasterApprentices', component: () => import('@/views/workspace/master/ApprenticeManage.vue'), meta: { title: '我的徒弟' } },
            { path: 'apprentices/:id/growth', name: 'MasterApprenticeGrowth', component: () => import('@/views/workspace/master/ApprenticeGrowth.vue'), meta: { title: '徒弟成长记录' } },
            { path: 'tasks', name: 'MasterTasks', component: () => import('@/views/workspace/master/TaskManage.vue'), meta: { title: '教学任务' } },
            { path: 'tasks/:id', name: 'MasterTaskDetail', component: () => import('@/views/workspace/master/TaskDetail.vue'), meta: { title: '任务详情' } },
            { path: 'artworks', name: 'MasterArtworks', component: () => import('@/views/workspace/master/ArtworkReview.vue'), meta: { title: '徒弟作品' } },
            { path: 'materials', name: 'MasterMaterials', component: () => import('@/views/workspace/master/MaterialManage.vue'), meta: { title: '学习材料' } },
          ]
        },
        // 徒弟工作台
        {
          path: 'workspace/apprentice',
          meta: { requiresAuth: true, role: 'apprentice' },
          children: [
            { path: '', redirect: '/workspace/apprentice/dashboard' },
            { path: 'dashboard', name: 'ApprenticeDashboard', component: () => import('@/views/workspace/apprentice/Dashboard.vue'), meta: { title: '徒弟工作台' } },
            { path: 'masters', name: 'ApprenticeMasters', component: () => import('@/views/workspace/apprentice/MyMasters.vue'), meta: { title: '我的师傅' } },
            { path: 'tasks', name: 'ApprenticeTasks', component: () => import('@/views/workspace/apprentice/MyTasks.vue'), meta: { title: '学习任务' } },
            { path: 'tasks/:id', name: 'ApprenticeTaskDetail', component: () => import('@/views/workspace/apprentice/TaskDetail.vue'), meta: { title: '任务详情' } },
            { path: 'artworks', name: 'ApprenticeArtworks', component: () => import('@/views/workspace/apprentice/MyArtworks.vue'), meta: { title: '我的作品' } },
            { path: 'growth', name: 'ApprenticeGrowth', component: () => import('@/views/workspace/apprentice/GrowthRecord.vue'), meta: { title: '成长记录' } },
          ]
        },
      ]
    },
    { path: '/login', name: 'Login', component: () => import('@/views/Login.vue'), meta: { title: '登录' } },
    { path: '/register', name: 'Register', component: () => import('@/views/Register.vue'), meta: { title: '注册' } },
    { path: '/:pathMatch(.*)*', name: 'NotFound', component: () => import('@/views/NotFound.vue'), meta: { title: '页面未找到' } },
  ]
})

// 路由守卫
router.beforeEach((to, _from, next) => {
  document.title = `${to.meta.title || '首页'} - 非遗传承管理系统`
  const token = localStorage.getItem('token')
  const user = localStorage.getItem('user')

  if (to.meta.requiresAuth && !token) {
    return next({ path: '/login', query: { redirect: to.fullPath } })
  }

  if (to.meta.role && user) {
    const parsed = JSON.parse(user)
    if (normalizeRole(parsed.role) !== to.meta.role) {
      return next('/')
    }
  }

  next()
})

export default router
