// ==================== 通用 ====================
export interface PageResult<T> {
  records: T[]
  total: number
  page: number
  size: number
}

export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

// ==================== 用户 ====================
export interface User {
  id: number
  username: string
  realName: string
  avatar: string | null
  phone: string
  email: string
  gender: number // 0-女 1-男
  role: 'admin' | 'master' | 'apprentice'
  status: number // 0-禁用 1-启用
  createdAt: string
  updatedAt: string
}

export interface LoginForm {
  username: string
  password: string
}

export interface RegisterForm {
  username: string
  password: string
  realName: string
  phone?: string
  email?: string
  gender?: number
  role: 'master' | 'apprentice'
}

// ==================== 技艺类别 ====================
export interface SkillCategory {
  id: number
  name: string
  parentId: number | null
  sortOrder: number
  description: string
  createdAt: string
  children?: SkillCategory[]
}

// ==================== 非遗项目 ====================
export interface HeritageProject {
  id: number
  name: string
  categoryId: number
  level: string
  description: string
  region: string
  imageUrl: string
  createdAt: string
  updatedAt: string
}

// ==================== 师傅档案 ====================
export interface MasterProfile {
  id: number
  userId: number
  title: string
  heritageProjectId: number
  skillCategoryId: number
  careerYears: number
  careerHistory: string
  specialties: string
  representativeWorks: string
  bio: string
  honor: string
  auditStatus: number // 0-待审核 1-通过 2-驳回
  createdAt: string
  updatedAt: string
  // 前端扩展（join 后）
  realName?: string
  avatar?: string
  projectName?: string
}

// ==================== 师徒关系 ====================
export interface Relation {
  id: number
  masterId: number
  apprenticeId: number
  heritageProjectId: number
  projectName?: string
  applyReason: string
  status: number // 0-待审核 1-已通过 2-已拒绝 3-已解除
  applyTime: string
  approveTime: string | null
  dissolveTime?: string | null
  dissolveReason?: string | null
  createdAt: string
  // 扩展
  masterName?: string
  masterAvatar?: string
  apprenticeName?: string
  apprenticeAvatar?: string
}

// ==================== 教学任务 ====================
export interface Task {
  id: number
  masterId: number
  apprenticeId: number | null
  title: string
  description: string
  heritageProjectId: number
  skillCategoryId: number
  status: number // 0-进行中 1-已完成 2-已取消
  deadline: string
  createdAt: string
  updatedAt: string
  masterName?: string
  apprenticeName?: string
  projectName?: string
}

// ==================== 任务提交 ====================
export interface Submission {
  id: number
  taskId: number
  apprenticeId: number
  apprenticeName?: string
  content: string
  fileUrl: string
  masterComment: string | null
  score: number | null
  createdAt: string
  updatedAt: string
}

// ==================== 作品 ====================
export interface Artwork {
  id: number
  apprenticeId: number
  title: string
  description: string
  heritageProjectId: number
  skillCategoryId: number
  imageUrls: string
  videoUrl: string
  status: number // 0-草稿 1-已提交
  createdAt: string
  updatedAt: string
}

// ==================== 作品点评 ====================
export interface ArtworkReview {
  id: number
  artworkId: number
  masterId: number
  content: string
  score: number
  createdAt: string
}

// ==================== 成长记录 ====================
export interface GrowthRecord {
  id: number
  apprenticeId: number
  recordType: string
  relatedId: number
  title: string
  description: string
  score: number
  createdAt: string
}

// ==================== 活动课程 ====================
export interface Activity {
  id: number
  title: string
  description: string
  content: string
  coverUrl: string
  location: string
  startTime: string
  endTime: string
  maxParticipants: number
  heritageProjectId: number
  status: number // 0-未开始 1-进行中 2-已结束
  createdAt: string
  updatedAt: string
}

export interface ActivityParticipant {
  id: number
  activityId: number
  userId: number
  participatedAt: string
  createdAt: string
}

// ==================== 公告 ====================
export interface Announcement {
  id: number
  title: string
  content: string
  isTop: number
  status: number // 0-草稿 1-已发布
  createdAt: string
  updatedAt: string
}

// ==================== 学习材料 ====================
export interface Material {
  id: number
  uploaderId: number
  title: string
  description: string
  fileUrl: string
  fileType: string
  fileSize: number
  taskId: number
  heritageProjectId: number
  createdAt: string
}

// ==================== 统计 ====================
export interface OverviewStats {
  totalUsers: number
  totalMasters: number
  totalApprentices: number
  totalProjects: number
  totalRelations: number
  totalTasks: number
  totalArtworks: number
}

export interface MasterApprenticeStats {
  masterId: number
  masterName: string
  apprenticeCount: number
  completedTasks: number
  totalArtworks: number
}

export interface CategoryArtworkStats {
  categoryId: number
  categoryName: string
  artworkCount: number
  submittedCount: number
}

export interface MonthlyGrowthStats {
  month: string
  newUsers: number
  newRelations: number
  newTasks: number
  newArtworks: number
}

export interface ActivityParticipationStats {
  activityId: number
  activityTitle: string
  totalParticipants: number
  maxParticipants: number
  participationRate: number
}
