# 非遗技艺师徒档案传承管理系统 API 文档

## 目录

- [系统信息](#系统信息)
- [认证说明](#认证说明)
- [响应格式](#响应格式)
- [测试账号](#测试账号)
- [API 接口](#api-接口)
  - [1. 认证模块](#1-认证模块)
  - [2. 用户管理](#2-用户管理)
  - [3. 技艺类别](#3-技艺类别)
  - [4. 非遗项目](#4-非遗项目)
  - [5. 师傅档案](#5-师傅档案)
  - [6. 师徒关系](#6-师徒关系)
  - [7. 教学任务](#7-教学任务)
  - [8. 任务提交](#8-任务提交)
  - [9. 作品](#9-作品)
  - [10. 作品点评](#10-作品点评)
  - [11. 成长记录](#11-成长记录)
  - [12. 活动课程](#12-活动课程)
  - [13. 公告](#13-公告)
  - [14. 学习材料](#14-学习材料)
  - [15. 审计日志](#15-审计日志)
  - [16. 统计](#16-统计)
  - [17. 文件](#17-文件)

---

## 系统信息

- **Base URL**: `http://localhost:8080`
- **认证方式**: JWT Bearer Token
- **请求头**: `Authorization: Bearer <token>`
- **Content-Type**: `application/json`
- **分页参数**: `page` (页码，默认1), `size` (每页数量，默认10)

---

## 认证说明

大部分接口需要认证，请在请求头中添加：

```
Authorization: Bearer <token>
```

登录成功后，响应中的 `data.token` 字段即为 JWT Token。

---

## 响应格式

### 成功响应

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

### 错误响应

```json
{
  "code": 400/401/403/404/500,
  "message": "错误描述",
  "data": null
}
```

### 分页响应

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [...],
    "total": 100,
    "page": 1,
    "size": 10
  }
}
```

---

## 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 师傅 | master1~3 | 123456 |
| 徒弟 | apprentice1~8 | 123456 |

---

## API 接口

### 1. 认证模块

#### 1.1 用户登录

**接口**: `POST /api/auth/login`

**描述**: 用户登录，获取 JWT Token

**请求头**: 无

**请求体**:
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**字段说明**:
- `username` (string, 必填): 用户名
- `password` (string, 必填): 密码

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "username": "admin",
      "realName": "管理员",
      "role": "admin",
      "status": 1,
      "createdAt": "2024-01-01T00:00:00"
    }
  }
}
```

---

#### 1.2 用户注册

**接口**: `POST /api/auth/register`

**描述**: 新用户注册

**请求头**: 无

**请求体**:
```json
{
  "username": "newuser",
  "password": "123456",
  "realName": "新用户",
  "phone": "13800138000",
  "email": "user@example.com",
  "gender": 1,
  "role": "apprentice"
}
```

**字段说明**:
- `username` (string, 必填): 用户名
- `password` (string, 必填): 密码
- `realName` (string, 必填): 真实姓名
- `phone` (string, 可选): 手机号
- `email` (string, 可选): 邮箱
- `gender` (integer, 可选): 性别 (0-女, 1-男)
- `role` (string, 必填): 角色 (admin/master/apprentice)

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 10,
    "username": "newuser",
    "realName": "新用户",
    "role": "apprentice",
    "status": 1,
    "createdAt": "2024-01-01T00:00:00"
  }
}
```

---

#### 1.3 获取当前用户信息

**接口**: `GET /api/auth/info`

**描述**: 获取当前登录用户信息

**请求头**: 
```
Authorization: Bearer <token>
```

**请求参数**: 无

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "admin",
    "realName": "管理员",
    "avatar": null,
    "phone": "13800138000",
    "email": "admin@example.com",
    "gender": 1,
    "role": "admin",
    "status": 1,
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00"
  }
}
```

---

### 2. 用户管理

#### 2.1 用户列表

**接口**: `GET /api/users`

**描述**: 获取用户列表（管理员）

**请求头**: 
```
Authorization: Bearer <token>
```

**请求参数**:
- `page` (integer, 可选): 页码，默认1
- `size` (integer, 可选): 每页数量，默认10
- `role` (string, 可选): 角色筛选 (admin/master/apprentice)
- `keyword` (string, 可选): 关键词搜索（用户名/真实姓名）

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "username": "admin",
        "realName": "管理员",
        "role": "admin",
        "status": 1,
        "createdAt": "2024-01-01T00:00:00"
      }
    ],
    "total": 50,
    "page": 1,
    "size": 10
  }
}
```

---

#### 2.2 用户详情

**接口**: `GET /api/users/{id}`

**描述**: 获取指定用户详情

**请求头**: 
```
Authorization: Bearer <token>
```

**路径参数**:
- `id` (long): 用户ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "admin",
    "realName": "管理员",
    "avatar": null,
    "phone": "13800138000",
    "email": "admin@example.com",
    "gender": 1,
    "role": "admin",
    "status": 1,
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00"
  }
}
```

---

#### 2.3 更新个人资料

**接口**: `PUT /api/users/profile`

**描述**: 更新当前登录用户的个人资料

**请求头**: 
```
Authorization: Bearer <token>
```

**请求体**:
```json
{
  "realName": "新姓名",
  "avatar": "/uploads/avatar.jpg",
  "phone": "13800138000",
  "email": "newemail@example.com",
  "gender": 1
}
```

**字段说明**:
- `realName` (string, 可选): 真实姓名
- `avatar` (string, 可选): 头像URL
- `phone` (string, 可选): 手机号
- `email` (string, 可选): 邮箱
- `gender` (integer, 可选): 性别 (0-女, 1-男)

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 2.4 修改密码

**接口**: `PUT /api/users/password`

**描述**: 修改当前登录用户的密码

**请求头**: 
```
Authorization: Bearer <token>
```

**请求体**:
```json
{
  "oldPassword": "old123456",
  "newPassword": "new123456"
}
```

**字段说明**:
- `oldPassword` (string, 必填): 旧密码
- `newPassword` (string, 必填): 新密码

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 2.5 更新用户状态

**接口**: `PUT /api/users/{id}/status`

**描述**: 更新用户状态（管理员）

**请求头**: 
```
Authorization: Bearer <token>
```

**路径参数**:
- `id` (long): 用户ID

**请求参数**:
- `status` (integer, 必填): 状态 (0-禁用, 1-启用)

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

### 3. 技艺类别

#### 3.1 类别列表

**接口**: `GET /api/skill-categories`

**描述**: 获取所有技艺类别列表

**请求头**: 无

**请求参数**: 无

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "传统手工艺",
      "parentId": null,
      "sortOrder": 1,
      "description": "传统手工艺类别",
      "createdAt": "2024-01-01T00:00:00"
    }
  ]
}
```

---

#### 3.2 类别树

**接口**: `GET /api/skill-categories/tree`

**描述**: 获取技艺类别树形结构

**请求头**: 无

**请求参数**: 无

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "传统手工艺",
      "parentId": null,
      "children": [
        {
          "id": 2,
          "name": "陶瓷工艺",
          "parentId": 1,
          "children": []
        }
      ]
    }
  ]
}
```

---

#### 3.3 创建类别

**接口**: `POST /api/skill-categories`

**描述**: 创建技艺类别（管理员）

**请求头**: 
```
Authorization: Bearer <token>
```

**请求体**:
```json
{
  "name": "新类别",
  "parentId": null,
  "sortOrder": 1,
  "description": "类别描述"
}
```

**字段说明**:
- `name` (string, 必填): 类别名称
- `parentId` (long, 可选): 父类别ID，null表示顶级类别
- `sortOrder` (integer, 可选): 排序顺序
- `description` (string, 可选): 类别描述

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 3.4 更新类别

**接口**: `PUT /api/skill-categories/{id}`

**描述**: 更新技艺类别（管理员）

**请求头**: 
```
Authorization: Bearer <token>
```

**路径参数**:
- `id` (long): 类别ID

**请求体**:
```json
{
  "name": "更新后的类别名",
  "parentId": null,
  "sortOrder": 2,
  "description": "更新后的描述"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 3.5 删除类别

**接口**: `DELETE /api/skill-categories/{id}`

**描述**: 删除技艺类别（管理员）

**请求头**: 
```
Authorization: Bearer <token>
```

**路径参数**:
- `id` (long): 类别ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

### 4. 非遗项目

#### 4.1 项目列表

**接口**: `GET /api/heritage-projects`

**描述**: 获取非遗项目列表

**请求头**: 无

**请求参数**:
- `page` (integer, 可选): 页码，默认1
- `size` (integer, 可选): 每页数量，默认10
- `keyword` (string, 可选): 关键词搜索（项目名称）
- `categoryId` (long, 可选): 类别ID筛选
- `level` (string, 可选): 级别筛选（国家级/省级/市级等）

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "name": "景德镇陶瓷制作技艺",
        "categoryId": 1,
        "level": "国家级",
        "description": "项目描述",
        "region": "江西省",
        "imageUrl": "/uploads/project.jpg",
        "createdAt": "2024-01-01T00:00:00"
      }
    ],
    "total": 20,
    "page": 1,
    "size": 10
  }
}
```

---

#### 4.2 所有项目

**接口**: `GET /api/heritage-projects/all`

**描述**: 获取所有非遗项目（不分页）

**请求头**: 无

**请求参数**: 无

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "景德镇陶瓷制作技艺",
      "categoryId": 1,
      "level": "国家级",
      "description": "项目描述",
      "region": "江西省",
      "imageUrl": "/uploads/project.jpg"
    }
  ]
}
```

---

#### 4.3 项目详情

**接口**: `GET /api/heritage-projects/{id}`

**描述**: 获取指定非遗项目详情

**请求头**: 无

**路径参数**:
- `id` (long): 项目ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "景德镇陶瓷制作技艺",
    "categoryId": 1,
    "level": "国家级",
    "description": "项目详细描述",
    "region": "江西省",
    "imageUrl": "/uploads/project.jpg",
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00"
  }
}
```

---

#### 4.4 创建项目

**接口**: `POST /api/heritage-projects`

**描述**: 创建非遗项目（管理员）

**请求头**: 
```
Authorization: Bearer <token>
```

**请求体**:
```json
{
  "name": "新非遗项目",
  "categoryId": 1,
  "level": "国家级",
  "description": "项目描述",
  "region": "北京市",
  "imageUrl": "/uploads/project.jpg"
}
```

**字段说明**:
- `name` (string, 必填): 项目名称
- `categoryId` (long, 可选): 类别ID
- `level` (string, 可选): 级别
- `description` (string, 可选): 项目描述
- `region` (string, 可选): 所属地区
- `imageUrl` (string, 可选): 项目图片URL

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 4.5 更新项目

**接口**: `PUT /api/heritage-projects/{id}`

**描述**: 更新非遗项目（管理员）

**请求头**: 
```
Authorization: Bearer <token>
```

**路径参数**:
- `id` (long): 项目ID

**请求体**:
```json
{
  "name": "更新后的项目名",
  "categoryId": 1,
  "level": "国家级",
  "description": "更新后的描述",
  "region": "北京市",
  "imageUrl": "/uploads/project.jpg"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 4.6 删除项目

**接口**: `DELETE /api/heritage-projects/{id}`

**描述**: 删除非遗项目（管理员）

**请求头**: 
```
Authorization: Bearer <token>
```

**路径参数**:
- `id` (long): 项目ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

### 5. 师傅档案

#### 5.1 我的档案

**接口**: `GET /api/master-profiles/my`

**描述**: 获取当前登录师傅的档案

**请求头**: 
```
Authorization: Bearer <token>
```

**请求参数**: 无

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "userId": 2,
    "title": "国家级非遗传承人",
    "heritageProjectId": 1,
    "skillCategoryId": 1,
    "careerYears": 30,
    "careerHistory": "从艺经历...",
    "specialties": "陶瓷制作、釉彩工艺",
    "representativeWorks": "[{\"name\":\"作品1\",\"url\":\"/uploads/work1.jpg\"}]",
    "bio": "个人简介...",
    "honor": "国家级传承人、工艺美术大师",
    "auditStatus": 1,
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00"
  }
}
```

---

#### 5.2 查看档案

**接口**: `GET /api/master-profiles/{userId}`

**描述**: 查看指定用户的师傅档案

**请求头**: 无

**路径参数**:
- `userId` (long): 用户ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "userId": 2,
    "title": "国家级非遗传承人",
    "heritageProjectId": 1,
    "skillCategoryId": 1,
    "careerYears": 30,
    "careerHistory": "从艺经历...",
    "specialties": "陶瓷制作、釉彩工艺",
    "representativeWorks": "[{\"name\":\"作品1\",\"url\":\"/uploads/work1.jpg\"}]",
    "bio": "个人简介...",
    "honor": "国家级传承人、工艺美术大师",
    "auditStatus": 1
  }
}
```

---

#### 5.3 创建/更新档案

**接口**: `POST /api/master-profiles`

**描述**: 创建或更新师傅档案（师傅）

**请求头**: 
```
Authorization: Bearer <token>
```

**请求体**:
```json
{
  "title": "国家级非遗传承人",
  "heritageProjectId": 1,
  "skillCategoryId": 1,
  "careerYears": 30,
  "careerHistory": "从艺经历...",
  "specialties": "陶瓷制作、釉彩工艺",
  "representativeWorks": "[{\"name\":\"作品1\",\"url\":\"/uploads/work1.jpg\"}]",
  "bio": "个人简介...",
  "honor": "国家级传承人、工艺美术大师"
}
```

**字段说明**:
- `title` (string, 可选): 称号/头衔
- `heritageProjectId` (long, 可选): 非遗项目ID
- `skillCategoryId` (long, 可选): 技艺类别ID
- `careerYears` (integer, 可选): 从艺年数
- `careerHistory` (string, 可选): 从艺经历
- `specialties` (string, 可选): 擅长领域
- `representativeWorks` (string, 可选): 代表作品（JSON字符串）
- `bio` (string, 可选): 个人简介
- `honor` (string, 可选): 荣誉称号

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 5.4 档案列表

**接口**: `GET /api/master-profiles`

**描述**: 获取师傅档案列表（管理员）

**请求头**: 
```
Authorization: Bearer <token>
```

**请求参数**:
- `page` (integer, 可选): 页码，默认1
- `size` (integer, 可选): 每页数量，默认10
- `auditStatus` (integer, 可选): 审核状态 (0-待审核, 1-通过, 2-驳回)

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "userId": 2,
        "title": "国家级非遗传承人",
        "heritageProjectId": 1,
        "auditStatus": 1,
        "createdAt": "2024-01-01T00:00:00"
      }
    ],
    "total": 10,
    "page": 1,
    "size": 10
  }
}
```

---

#### 5.5 审核档案

**接口**: `PUT /api/master-profiles/{id}/audit`

**描述**: 审核师傅档案（管理员）

**请求头**: 
```
Authorization: Bearer <token>
```

**路径参数**:
- `id` (long): 档案ID

**请求参数**:
- `auditStatus` (integer, 必填): 审核状态 (1-通过, 2-驳回)

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

### 6. 师徒关系

#### 6.1 申请拜师

**接口**: `POST /api/relations/apply`

**描述**: 徒弟申请拜师

**请求头**: 
```
Authorization: Bearer <token>
```

**请求体**:
```json
{
  "masterId": 2,
  "heritageProjectId": 1,
  "applyReason": "希望学习传统陶瓷制作技艺"
}
```

**字段说明**:
- `masterId` (long, 必填): 师傅用户ID
- `heritageProjectId` (long, 可选): 非遗项目ID
- `applyReason` (string, 可选): 拜师理由

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 6.2 审核申请

**接口**: `PUT /api/relations/{id}/approve`

**描述**: 师傅审核拜师申请

**请求头**: 
```
Authorization: Bearer <token>
```

**路径参数**:
- `id` (long): 关系ID

**请求参数**:
- `approved` (boolean, 必填): 是否通过 (true-通过, false-拒绝)

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 6.3 解除关系

**接口**: `PUT /api/relations/{id}/dissolve`

**描述**: 解除师徒关系

**请求头**: 
```
Authorization: Bearer <token>
```

**路径参数**:
- `id` (long): 关系ID

**请求参数**:
- `reason` (string, 可选): 解除原因

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 6.4 我的徒弟

**接口**: `GET /api/relations/master`

**描述**: 获取当前师傅的所有徒弟

**请求头**: 
```
Authorization: Bearer <token>
```

**请求参数**: 无

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "masterId": 2,
      "apprenticeId": 3,
      "heritageProjectId": 1,
      "applyReason": "希望学习传统陶瓷制作技艺",
      "status": 1,
      "applyTime": "2024-01-01T00:00:00",
      "approveTime": "2024-01-02T00:00:00",
      "createdAt": "2024-01-01T00:00:00"
    }
  ]
}
```

---

#### 6.5 我的师傅

**接口**: `GET /api/relations/apprentice`

**描述**: 获取当前徒弟的所有师傅

**请求头**: 
```
Authorization: Bearer <token>
```

**请求参数**: 无

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "masterId": 2,
      "apprenticeId": 3,
      "heritageProjectId": 1,
      "applyReason": "希望学习传统陶瓷制作技艺",
      "status": 1,
      "applyTime": "2024-01-01T00:00:00",
      "approveTime": "2024-01-02T00:00:00",
      "createdAt": "2024-01-01T00:00:00"
    }
  ]
}
```

---

#### 6.6 关系列表

**接口**: `GET /api/relations`

**描述**: 获取所有师徒关系列表（管理员）

**请求头**: 
```
Authorization: Bearer <token>
```

**请求参数**:
- `page` (integer, 可选): 页码，默认1
- `size` (integer, 可选): 每页数量，默认10
- `status` (integer, 可选): 状态筛选 (0-待审核, 1-已通过, 2-已拒绝, 3-已解除)

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "masterId": 2,
        "apprenticeId": 3,
        "heritageProjectId": 1,
        "status": 1,
        "applyTime": "2024-01-01T00:00:00",
        "approveTime": "2024-01-02T00:00:00"
      }
    ],
    "total": 50,
    "page": 1,
    "size": 10
  }
}
```

---

### 7. 教学任务

#### 7.1 创建任务

**接口**: `POST /api/tasks`

**描述**: 师傅创建教学任务

**请求头**: 
```
Authorization: Bearer <token>
```

**请求体**:
```json
{
  "title": "学习基础拉坯技法",
  "description": "任务描述...",
  "heritageProjectId": 1,
  "skillCategoryId": 1,
  "deadline": "2024-12-31T23:59:59"
}
```

**字段说明**:
- `title` (string, 必填): 任务标题
- `description` (string, 可选): 任务描述
- `heritageProjectId` (long, 可选): 非遗项目ID
- `skillCategoryId` (long, 可选): 技艺类别ID
- `deadline` (string, 可选): 截止时间 (ISO 8601格式)

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 7.2 更新任务

**接口**: `PUT /api/tasks/{id}`

**描述**: 师傅更新教学任务

**请求头**: 
```
Authorization: Bearer <token>
```

**路径参数**:
- `id` (long): 任务ID

**请求体**:
```json
{
  "title": "更新后的任务标题",
  "description": "更新后的描述",
  "heritageProjectId": 1,
  "skillCategoryId": 1,
  "deadline": "2024-12-31T23:59:59"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 7.3 更新任务状态

**接口**: `PUT /api/tasks/{id}/status`

**描述**: 师傅更新任务状态

**请求头**: 
```
Authorization: Bearer <token>
```

**路径参数**:
- `id` (long): 任务ID

**请求参数**:
- `status` (integer, 必填): 状态 (0-进行中, 1-已完成, 2-已取消)

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 7.4 我的任务（师傅）

**接口**: `GET /api/tasks/master`

**描述**: 获取当前师傅发布的所有任务

**请求头**: 
```
Authorization: Bearer <token>
```

**请求参数**:
- `page` (integer, 可选): 页码，默认1
- `size` (integer, 可选): 每页数量，默认10

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "masterId": 2,
        "title": "学习基础拉坯技法",
        "description": "任务描述...",
        "heritageProjectId": 1,
        "skillCategoryId": 1,
        "status": 0,
        "deadline": "2024-12-31T23:59:59",
        "createdAt": "2024-01-01T00:00:00"
      }
    ],
    "total": 10,
    "page": 1,
    "size": 10
  }
}
```

---

#### 7.5 我的任务（徒弟）

**接口**: `GET /api/tasks/apprentice`

**描述**: 获取当前徒弟的所有任务

**请求头**: 
```
Authorization: Bearer <token>
```

**请求参数**:
- `page` (integer, 可选): 页码，默认1
- `size` (integer, 可选): 每页数量，默认10

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "masterId": 2,
        "title": "学习基础拉坯技法",
        "description": "任务描述...",
        "heritageProjectId": 1,
        "skillCategoryId": 1,
        "status": 0,
        "deadline": "2024-12-31T23:59:59",
        "createdAt": "2024-01-01T00:00:00"
      }
    ],
    "total": 5,
    "page": 1,
    "size": 10
  }
}
```

---

#### 7.6 任务详情

**接口**: `GET /api/tasks/{id}`

**描述**: 获取指定任务详情

**请求头**: 无

**路径参数**:
- `id` (long): 任务ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "masterId": 2,
    "title": "学习基础拉坯技法",
    "description": "任务详细描述...",
    "heritageProjectId": 1,
    "skillCategoryId": 1,
    "status": 0,
    "deadline": "2024-12-31T23:59:59",
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00"
  }
}
```

---

#### 7.7 任务列表

**接口**: `GET /api/tasks`

**描述**: 获取所有任务列表（管理员）

**请求头**: 
```
Authorization: Bearer <token>
```

**请求参数**:
- `page` (integer, 可选): 页码，默认1
- `size` (integer, 可选): 每页数量，默认10
- `status` (integer, 可选): 状态筛选 (0-进行中, 1-已完成, 2-已取消)

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "masterId": 2,
        "title": "学习基础拉坯技法",
        "status": 0,
        "createdAt": "2024-01-01T00:00:00"
      }
    ],
    "total": 100,
    "page": 1,
    "size": 10
  }
}
```

---

### 8. 任务提交

#### 8.1 提交任务

**接口**: `POST /api/submissions`

**描述**: 徒弟提交任务学习成果

**请求头**: 
```
Authorization: Bearer <token>
```

**请求体**:
```json
{
  "taskId": 1,
  "content": "提交内容...",
  "fileUrl": "/uploads/submission.pdf"
}
```

**字段说明**:
- `taskId` (long, 必填): 任务ID
- `content` (string, 可选): 提交内容
- `fileUrl` (string, 可选): 附件文件URL

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 8.2 任务的提交列表

**接口**: `GET /api/submissions/task/{taskId}`

**描述**: 获取指定任务的所有提交

**请求头**: 无

**路径参数**:
- `taskId` (long): 任务ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "taskId": 1,
      "apprenticeId": 3,
      "content": "提交内容...",
      "fileUrl": "/uploads/submission.pdf",
      "masterComment": null,
      "score": null,
      "createdAt": "2024-01-01T00:00:00"
    }
  ]
}
```

---

#### 8.3 我的提交

**接口**: `GET /api/submissions/my/{taskId}`

**描述**: 获取当前徒弟对指定任务的提交

**请求头**: 
```
Authorization: Bearer <token>
```

**路径参数**:
- `taskId` (long): 任务ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "taskId": 1,
    "apprenticeId": 3,
    "content": "提交内容...",
    "fileUrl": "/uploads/submission.pdf",
    "masterComment": "完成得很好",
    "score": 95,
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-02T00:00:00"
  }
}
```

---

#### 8.4 点评提交

**接口**: `PUT /api/submissions/{id}/review`

**描述**: 师傅点评任务提交

**请求头**: 
```
Authorization: Bearer <token>
```

**路径参数**:
- `id` (long): 提交ID

**请求体**:
```json
{
  "masterComment": "完成得很好，继续努力",
  "score": 95
}
```

**字段说明**:
- `masterComment` (string, 可选): 师傅点评
- `score` (integer, 可选): 评分 (0-100)

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

### 9. 作品

#### 9.1 创建作品

**接口**: `POST /api/artworks`

**描述**: 徒弟创建作品

**请求头**: 
```
Authorization: Bearer <token>
```

**请求体**:
```json
{
  "title": "我的第一件陶瓷作品",
  "description": "作品描述...",
  "heritageProjectId": 1,
  "skillCategoryId": 1,
  "imageUrls": "/uploads/artwork1.jpg,/uploads/artwork2.jpg",
  "videoUrl": "/uploads/artwork.mp4"
}
```

**字段说明**:
- `title` (string, 必填): 作品标题
- `description` (string, 可选): 作品描述
- `heritageProjectId` (long, 可选): 非遗项目ID
- `skillCategoryId` (long, 可选): 技艺类别ID
- `imageUrls` (string, 可选): 图片URL列表（逗号分隔）
- `videoUrl` (string, 可选): 视频URL

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 9.2 更新作品

**接口**: `PUT /api/artworks/{id}`

**描述**: 徒弟更新作品

**请求头**: 
```
Authorization: Bearer <token>
```

**路径参数**:
- `id` (long): 作品ID

**请求体**:
```json
{
  "title": "更新后的作品标题",
  "description": "更新后的描述",
  "heritageProjectId": 1,
  "skillCategoryId": 1,
  "imageUrls": "/uploads/artwork1.jpg,/uploads/artwork2.jpg",
  "videoUrl": "/uploads/artwork.mp4"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 9.3 提交作品

**接口**: `PUT /api/artworks/{id}/submit`

**描述**: 徒弟提交作品（提交后不可修改）

**请求头**: 
```
Authorization: Bearer <token>
```

**路径参数**:
- `id` (long): 作品ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 9.4 我的作品

**接口**: `GET /api/artworks/my`

**描述**: 获取当前徒弟的所有作品

**请求头**: 
```
Authorization: Bearer <token>
```

**请求参数**:
- `page` (integer, 可选): 页码，默认1
- `size` (integer, 可选): 每页数量，默认10

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "apprenticeId": 3,
        "title": "我的第一件陶瓷作品",
        "heritageProjectId": 1,
        "skillCategoryId": 1,
        "status": 0,
        "createdAt": "2024-01-01T00:00:00"
      }
    ],
    "total": 5,
    "page": 1,
    "size": 10
  }
}
```

---

#### 9.5 徒弟作品

**接口**: `GET /api/artworks/master`

**描述**: 获取当前师傅的所有徒弟的作品

**请求头**: 
```
Authorization: Bearer <token>
```

**请求参数**:
- `page` (integer, 可选): 页码，默认1
- `size` (integer, 可选): 每页数量，默认10

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "apprenticeId": 3,
        "title": "我的第一件陶瓷作品",
        "heritageProjectId": 1,
        "skillCategoryId": 1,
        "status": 1,
        "createdAt": "2024-01-01T00:00:00"
      }
    ],
    "total": 20,
    "page": 1,
    "size": 10
  }
}
```

---

#### 9.6 作品详情

**接口**: `GET /api/artworks/{id}`

**描述**: 获取指定作品详情

**请求头**: 无

**路径参数**:
- `id` (long): 作品ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "apprenticeId": 3,
    "title": "我的第一件陶瓷作品",
    "description": "作品详细描述...",
    "heritageProjectId": 1,
    "skillCategoryId": 1,
    "imageUrls": "/uploads/artwork1.jpg,/uploads/artwork2.jpg",
    "videoUrl": "/uploads/artwork.mp4",
    "status": 1,
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00"
  }
}
```

---

#### 9.7 作品列表

**接口**: `GET /api/artworks`

**描述**: 获取所有作品列表（管理员）

**请求头**: 
```
Authorization: Bearer <token>
```

**请求参数**:
- `page` (integer, 可选): 页码，默认1
- `size` (integer, 可选): 每页数量，默认10
- `status` (integer, 可选): 状态筛选 (0-草稿, 1-已提交)

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "apprenticeId": 3,
        "title": "我的第一件陶瓷作品",
        "heritageProjectId": 1,
        "status": 1,
        "createdAt": "2024-01-01T00:00:00"
      }
    ],
    "total": 100,
    "page": 1,
    "size": 10
  }
}
```

---

#### 9.8 删除作品

**接口**: `DELETE /api/artworks/{id}`

**描述**: 删除作品（管理员）

**请求头**: 
```
Authorization: Bearer <token>
```

**路径参数**:
- `id` (long): 作品ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

### 10. 作品点评

#### 10.1 创建点评

**接口**: `POST /api/artwork-reviews`

**描述**: 师傅对徒弟作品进行点评

**请求头**: 
```
Authorization: Bearer <token>
```

**请求体**:
```json
{
  "artworkId": 1,
  "content": "作品点评内容...",
  "score": 90
}
```

**字段说明**:
- `artworkId` (long, 必填): 作品ID
- `content` (string, 必填): 点评内容
- `score` (integer, 可选): 评分 (0-100)

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 10.2 作品的点评列表

**接口**: `GET /api/artwork-reviews/artwork/{artworkId}`

**描述**: 获取指定作品的所有点评

**请求头**: 无

**路径参数**:
- `artworkId` (long): 作品ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "artworkId": 1,
      "masterId": 2,
      "content": "作品点评内容...",
      "score": 90,
      "createdAt": "2024-01-01T00:00:00"
    }
  ]
}
```

---

### 11. 成长记录

#### 11.1 我的记录

**接口**: `GET /api/growth-records/my`

**描述**: 获取当前徒弟的成长记录

**请求头**: 
```
Authorization: Bearer <token>
```

**请求参数**:
- `page` (integer, 可选): 页码，默认1
- `size` (integer, 可选): 每页数量，默认10
- `recordType` (string, 可选): 记录类型筛选 (task_submission/artwork/review等)

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "apprenticeId": 3,
        "recordType": "task_submission",
        "relatedId": 1,
        "title": "完成任务：学习基础拉坯技法",
        "description": "完成了基础拉坯技法的学习任务",
        "score": 95,
        "createdAt": "2024-01-01T00:00:00"
      }
    ],
    "total": 20,
    "page": 1,
    "size": 10
  }
}
```

---

#### 11.2 徒弟记录

**接口**: `GET /api/growth-records/apprentice/{apprenticeId}`

**描述**: 获取指定徒弟的成长记录（师傅/管理员）

**请求头**: 
```
Authorization: Bearer <token>
```

**路径参数**:
- `apprenticeId` (long): 徒弟用户ID

**请求参数**:
- `page` (integer, 可选): 页码，默认1
- `size` (integer, 可选): 每页数量，默认10
- `recordType` (string, 可选): 记录类型筛选

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "apprenticeId": 3,
        "recordType": "task_submission",
        "relatedId": 1,
        "title": "完成任务：学习基础拉坯技法",
        "description": "完成了基础拉坯技法的学习任务",
        "score": 95,
        "createdAt": "2024-01-01T00:00:00"
      }
    ],
    "total": 20,
    "page": 1,
    "size": 10
  }
}
```

---

### 12. 活动课程

#### 12.1 创建活动

**接口**: `POST /api/activities`

**描述**: 创建活动（管理员）

**请求头**: 
```
Authorization: Bearer <token>
```

**请求体**:
```json
{
  "title": "传统陶瓷制作体验活动",
  "description": "活动简介...",
  "content": "活动详细内容...",
  "coverUrl": "/uploads/activity.jpg",
  "location": "景德镇陶瓷博物馆",
  "startTime": "2024-12-01T09:00:00",
  "endTime": "2024-12-01T17:00:00",
  "maxParticipants": 50,
  "heritageProjectId": 1
}
```

**字段说明**:
- `title` (string, 必填): 活动标题
- `description` (string, 可选): 活动简介
- `content` (string, 可选): 活动详细内容
- `coverUrl` (string, 可选): 封面图片URL
- `location` (string, 可选): 活动地点
- `startTime` (string, 可选): 开始时间 (ISO 8601格式)
- `endTime` (string, 可选): 结束时间 (ISO 8601格式)
- `maxParticipants` (integer, 可选): 最大参与人数
- `heritageProjectId` (long, 可选): 非遗项目ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 12.2 更新活动

**接口**: `PUT /api/activities/{id}`

**描述**: 更新活动（管理员）

**请求头**: 
```
Authorization: Bearer <token>
```

**路径参数**:
- `id` (long): 活动ID

**请求体**:
```json
{
  "title": "更新后的活动标题",
  "description": "更新后的简介",
  "content": "更新后的详细内容",
  "coverUrl": "/uploads/activity.jpg",
  "location": "景德镇陶瓷博物馆",
  "startTime": "2024-12-01T09:00:00",
  "endTime": "2024-12-01T17:00:00",
  "maxParticipants": 50,
  "heritageProjectId": 1
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 12.3 删除活动

**接口**: `DELETE /api/activities/{id}`

**描述**: 删除活动（管理员）

**请求头**: 
```
Authorization: Bearer <token>
```

**路径参数**:
- `id` (long): 活动ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 12.4 活动列表

**接口**: `GET /api/activities`

**描述**: 获取活动列表

**请求头**: 无

**请求参数**:
- `page` (integer, 可选): 页码，默认1
- `size` (integer, 可选): 每页数量，默认10
- `status` (integer, 可选): 状态筛选 (0-未开始, 1-进行中, 2-已结束)

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "title": "传统陶瓷制作体验活动",
        "description": "活动简介...",
        "coverUrl": "/uploads/activity.jpg",
        "location": "景德镇陶瓷博物馆",
        "startTime": "2024-12-01T09:00:00",
        "endTime": "2024-12-01T17:00:00",
        "maxParticipants": 50,
        "status": 0,
        "createdAt": "2024-01-01T00:00:00"
      }
    ],
    "total": 10,
    "page": 1,
    "size": 10
  }
}
```

---

#### 12.5 即将开始的活动

**接口**: `GET /api/activities/upcoming`

**描述**: 获取即将开始的活动列表

**请求头**: 无

**请求参数**: 无

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "title": "传统陶瓷制作体验活动",
      "description": "活动简介...",
      "coverUrl": "/uploads/activity.jpg",
      "location": "景德镇陶瓷博物馆",
      "startTime": "2024-12-01T09:00:00",
      "endTime": "2024-12-01T17:00:00",
      "maxParticipants": 50,
      "status": 0
    }
  ]
}
```

---

#### 12.6 活动详情

**接口**: `GET /api/activities/{id}`

**描述**: 获取指定活动详情

**请求头**: 无

**路径参数**:
- `id` (long): 活动ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "title": "传统陶瓷制作体验活动",
    "description": "活动简介...",
    "content": "活动详细内容...",
    "coverUrl": "/uploads/activity.jpg",
    "location": "景德镇陶瓷博物馆",
    "startTime": "2024-12-01T09:00:00",
    "endTime": "2024-12-01T17:00:00",
    "maxParticipants": 50,
    "heritageProjectId": 1,
    "status": 0,
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00"
  }
}
```

---

#### 12.7 报名参加

**接口**: `POST /api/activities/{id}/participate`

**描述**: 报名参加活动

**请求头**: 
```
Authorization: Bearer <token>
```

**路径参数**:
- `id` (long): 活动ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 12.8 取消参加

**接口**: `DELETE /api/activities/{id}/participate`

**描述**: 取消参加活动

**请求头**: 
```
Authorization: Bearer <token>
```

**路径参数**:
- `id` (long): 活动ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 12.9 参与者列表

**接口**: `GET /api/activities/{id}/participants`

**描述**: 获取活动的参与者列表

**请求头**: 无

**路径参数**:
- `id` (long): 活动ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "activityId": 1,
      "userId": 3,
      "participatedAt": "2024-01-01T00:00:00",
      "createdAt": "2024-01-01T00:00:00"
    }
  ]
}
```

---

### 13. 公告

#### 13.1 创建公告

**接口**: `POST /api/announcements`

**描述**: 创建公告（管理员）

**请求头**: 
```
Authorization: Bearer <token>
```

**请求体**:
```json
{
  "title": "系统维护通知",
  "content": "公告内容...",
  "isTop": 1,
  "status": 1
}
```

**字段说明**:
- `title` (string, 必填): 公告标题
- `content` (string, 必填): 公告内容
- `isTop` (integer, 可选): 是否置顶 (0-否, 1-是)
- `status` (integer, 可选): 状态 (0-草稿, 1-已发布)

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 13.2 更新公告

**接口**: `PUT /api/announcements/{id}`

**描述**: 更新公告（管理员）

**请求头**: 
```
Authorization: Bearer <token>
```

**路径参数**:
- `id` (long): 公告ID

**请求体**:
```json
{
  "title": "更新后的公告标题",
  "content": "更新后的公告内容",
  "isTop": 1,
  "status": 1
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 13.3 删除公告

**接口**: `DELETE /api/announcements/{id}`

**描述**: 删除公告（管理员）

**请求头**: 
```
Authorization: Bearer <token>
```

**路径参数**:
- `id` (long): 公告ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 13.4 公告列表

**接口**: `GET /api/announcements`

**描述**: 获取公告列表

**请求头**: 无

**请求参数**:
- `page` (integer, 可选): 页码，默认1
- `size` (integer, 可选): 每页数量，默认10
- `status` (integer, 可选): 状态筛选 (0-草稿, 1-已发布)

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "title": "系统维护通知",
        "content": "公告内容...",
        "isTop": 1,
        "status": 1,
        "createdAt": "2024-01-01T00:00:00"
      }
    ],
    "total": 20,
    "page": 1,
    "size": 10
  }
}
```

---

#### 13.5 已发布公告

**接口**: `GET /api/announcements/published`

**描述**: 获取所有已发布的公告（不分页）

**请求头**: 无

**请求参数**: 无

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "title": "系统维护通知",
      "content": "公告内容...",
      "isTop": 1,
      "status": 1,
      "createdAt": "2024-01-01T00:00:00"
    }
  ]
}
```

---

#### 13.6 公告详情

**接口**: `GET /api/announcements/{id}`

**描述**: 获取指定公告详情

**请求头**: 无

**路径参数**:
- `id` (long): 公告ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "title": "系统维护通知",
    "content": "公告详细内容...",
    "isTop": 1,
    "status": 1,
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00"
  }
}
```

---

### 14. 学习材料

#### 14.1 上传材料

**接口**: `POST /api/materials`

**描述**: 师傅上传学习材料

**请求头**: 
```
Authorization: Bearer <token>
```

**请求体**:
```json
{
  "title": "基础拉坯技法教程",
  "description": "材料描述...",
  "fileUrl": "/uploads/material.pdf",
  "fileType": "pdf",
  "fileSize": 1024000,
  "taskId": 1,
  "heritageProjectId": 1
}
```

**字段说明**:
- `title` (string, 必填): 材料标题
- `description` (string, 可选): 材料描述
- `fileUrl` (string, 必填): 文件URL
- `fileType` (string, 可选): 文件类型
- `fileSize` (long, 可选): 文件大小（字节）
- `taskId` (long, 可选): 关联任务ID
- `heritageProjectId` (long, 可选): 非遗项目ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 14.2 删除材料

**接口**: `DELETE /api/materials/{id}`

**描述**: 删除学习材料

**请求头**: 
```
Authorization: Bearer <token>
```

**路径参数**:
- `id` (long): 材料ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

#### 14.3 任务材料

**接口**: `GET /api/materials/task/{taskId}`

**描述**: 获取指定任务的学习材料

**请求头**: 无

**路径参数**:
- `taskId` (long): 任务ID

**请求参数**:
- `page` (integer, 可选): 页码，默认1
- `size` (integer, 可选): 每页数量，默认10

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "uploaderId": 2,
        "title": "基础拉坯技法教程",
        "description": "材料描述...",
        "fileUrl": "/uploads/material.pdf",
        "fileType": "pdf",
        "fileSize": 1024000,
        "taskId": 1,
        "createdAt": "2024-01-01T00:00:00"
      }
    ],
    "total": 5,
    "page": 1,
    "size": 10
  }
}
```

---

#### 14.4 我上传的材料

**接口**: `GET /api/materials/my`

**描述**: 获取当前用户上传的学习材料

**请求头**: 
```
Authorization: Bearer <token>
```

**请求参数**:
- `page` (integer, 可选): 页码，默认1
- `size` (integer, 可选): 每页数量，默认10

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "uploaderId": 2,
        "title": "基础拉坯技法教程",
        "fileUrl": "/uploads/material.pdf",
        "createdAt": "2024-01-01T00:00:00"
      }
    ],
    "total": 10,
    "page": 1,
    "size": 10
  }
}
```

---

#### 14.5 所有材料

**接口**: `GET /api/materials`

**描述**: 获取所有学习材料（管理员）

**请求头**: 
```
Authorization: Bearer <token>
```

**请求参数**:
- `page` (integer, 可选): 页码，默认1
- `size` (integer, 可选): 每页数量，默认10

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "uploaderId": 2,
        "title": "基础拉坯技法教程",
        "fileUrl": "/uploads/material.pdf",
        "createdAt": "2024-01-01T00:00:00"
      }
    ],
    "total": 50,
    "page": 1,
    "size": 10
  }
}
```

---

### 15. 审计日志

#### 15.1 日志列表

**接口**: `GET /api/audit-logs`

**描述**: 获取审计日志列表（管理员）

**请求头**: 
```
Authorization: Bearer <token>
```

**请求参数**:
- `page` (integer, 可选): 页码，默认1
- `size` (integer, 可选): 每页数量，默认10
- `operation` (string, 可选): 操作类型筛选
- `userId` (long, 可选): 用户ID筛选

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "userId": 1,
        "operation": "用户注册",
        "targetType": "user",
        "targetId": 10,
        "details": "注册了新用户",
        "ipAddress": "127.0.0.1",
        "createdAt": "2024-01-01T00:00:00"
      }
    ],
    "total": 1000,
    "page": 1,
    "size": 10
  }
}
```

---

### 16. 统计

#### 16.1 总览统计

**接口**: `GET /api/statistics/overview`

**描述**: 获取系统总览统计数据

**请求头**: 无

**请求参数**: 无

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalUsers": 100,
    "totalMasters": 10,
    "totalApprentices": 80,
    "totalProjects": 20,
    "totalRelations": 50,
    "totalTasks": 200,
    "totalArtworks": 500
  }
}
```

---

#### 16.2 师徒统计

**接口**: `GET /api/statistics/master-apprentice`

**描述**: 获取师徒关系统计数据

**请求头**: 无

**请求参数**: 无

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "masterId": 2,
      "masterName": "张师傅",
      "apprenticeCount": 5,
      "completedTasks": 20,
      "totalArtworks": 30
    }
  ]
}
```

---

#### 16.3 类别作品统计

**接口**: `GET /api/statistics/category-artwork`

**描述**: 获取各类别作品统计数据

**请求头**: 无

**请求参数**: 无

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "categoryId": 1,
      "categoryName": "传统手工艺",
      "artworkCount": 200,
      "submittedCount": 150
    }
  ]
}
```

---

#### 16.4 月度成长统计

**接口**: `GET /api/statistics/monthly-growth`

**描述**: 获取月度成长统计数据

**请求头**: 无

**请求参数**: 无

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "month": "2024-01",
      "newUsers": 10,
      "newRelations": 5,
      "newTasks": 20,
      "newArtworks": 50
    }
  ]
}
```

---

#### 16.5 活动参与统计

**接口**: `GET /api/statistics/activity-participation`

**描述**: 获取活动参与统计数据

**请求头**: 无

**请求参数**: 无

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "activityId": 1,
      "activityTitle": "传统陶瓷制作体验活动",
      "totalParticipants": 50,
      "maxParticipants": 50,
      "participationRate": 100
    }
  ]
}
```

---

### 17. 文件

#### 17.1 文件上传

**接口**: `POST /api/files/upload`

**描述**: 上传文件

**请求头**: 
```
Authorization: Bearer <token>
Content-Type: multipart/form-data
```

**请求参数**:
- `file` (file, 必填): 要上传的文件

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": "/uploads/abc123.jpg"
}
```

**错误响应示例**:
```json
{
  "code": 400,
  "message": "文件不能为空",
  "data": null
}
```

---

## 状态码说明

### 用户状态
- `0`: 禁用
- `1`: 启用

### 审核状态
- `0`: 待审核
- `1`: 通过
- `2`: 驳回

### 师徒关系状态
- `0`: 待审核
- `1`: 已通过
- `2`: 已拒绝
- `3`: 已解除

### 任务状态
- `0`: 进行中
- `1`: 已完成
- `2`: 已取消

### 作品状态
- `0`: 草稿
- `1`: 已提交

### 活动状态
- `0`: 未开始
- `1`: 进行中
- `2`: 已结束

### 公告状态
- `0`: 草稿
- `1`: 已发布

### 性别
- `0`: 女
- `1`: 男

### 角色
- `admin`: 管理员
- `master`: 师傅
- `apprentice`: 徒弟

---

## 注意事项

1. **认证**: 大部分接口需要 JWT Token 认证，请在请求头中添加 `Authorization: Bearer <token>`
2. **时间格式**: 所有时间字段使用 ISO 8601 格式，例如: `2024-01-01T00:00:00`
3. **分页**: 分页接口默认页码为 1，每页数量为 10
4. **文件上传**: 文件上传接口使用 `multipart/form-data` 格式，字段名为 `file`
5. **错误处理**: 所有错误响应都遵循统一的错误响应格式
6. **权限**: 部分接口需要特定角色权限，请确保当前用户具有相应权限

---

## 更新日志

- **2024-01-01**: 初始版本 API 文档
