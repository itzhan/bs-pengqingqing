-- ============================================================
-- 非遗技艺"师徒档案"传承管理系统 - 数据库初始化脚本
-- ============================================================

CREATE DATABASE IF NOT EXISTS heritage_archive DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE heritage_archive;

SET NAMES utf8mb4;
SET CHARACTER_SET_CLIENT = utf8mb4;
SET CHARACTER_SET_RESULTS = utf8mb4;
SET CHARACTER_SET_CONNECTION = utf8mb4;

-- -----------------------------------------------------------
-- 1. 用户表 sys_user
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `audit_log`;
DROP TABLE IF EXISTS `activity_participation`;
DROP TABLE IF EXISTS `growth_record`;
DROP TABLE IF EXISTS `artwork_review`;
DROP TABLE IF EXISTS `artwork`;
DROP TABLE IF EXISTS `task_submission`;
DROP TABLE IF EXISTS `learning_material`;
DROP TABLE IF EXISTS `teaching_task`;
DROP TABLE IF EXISTS `master_apprentice_relation`;
DROP TABLE IF EXISTS `master_profile`;
DROP TABLE IF EXISTS `heritage_project`;
DROP TABLE IF EXISTS `skill_category`;
DROP TABLE IF EXISTS `activity`;
DROP TABLE IF EXISTS `announcement`;
DROP TABLE IF EXISTS `sys_user`;

CREATE TABLE `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
    `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
    `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `gender` TINYINT DEFAULT 0 COMMENT '性别: 0-未知 1-男 2-女',
    `role` VARCHAR(20) NOT NULL COMMENT '角色: ADMIN/MASTER/APPRENTICE',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-正常',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at` DATETIME DEFAULT NULL COMMENT '删除时间(软删除)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_role` (`role`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- -----------------------------------------------------------
-- 2. 技艺类别表 skill_category
-- -----------------------------------------------------------
CREATE TABLE `skill_category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name` VARCHAR(50) NOT NULL COMMENT '类别名称',
    `parent_id` BIGINT DEFAULT NULL COMMENT '父级ID',
    `sort_order` INT DEFAULT 0 COMMENT '排序序号',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted_at` DATETIME DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='技艺类别表';

-- -----------------------------------------------------------
-- 3. 非遗项目表 heritage_project
-- -----------------------------------------------------------
CREATE TABLE `heritage_project` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name` VARCHAR(100) NOT NULL COMMENT '项目名称',
    `category_id` BIGINT DEFAULT NULL COMMENT '技艺类别ID',
    `level` VARCHAR(20) DEFAULT NULL COMMENT '级别: 国家级/省级/市级/县级',
    `description` TEXT COMMENT '项目描述',
    `region` VARCHAR(100) DEFAULT NULL COMMENT '所属地区',
    `image_url` VARCHAR(500) DEFAULT NULL COMMENT '封面图片',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-正常',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted_at` DATETIME DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_level` (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='非遗项目表';

-- -----------------------------------------------------------
-- 4. 师傅档案表 master_profile
-- -----------------------------------------------------------
CREATE TABLE `master_profile` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `title` VARCHAR(100) DEFAULT NULL COMMENT '称号/头衔',
    `heritage_project_id` BIGINT DEFAULT NULL COMMENT '非遗项目ID',
    `skill_category_id` BIGINT DEFAULT NULL COMMENT '技艺类别ID',
    `career_years` INT DEFAULT NULL COMMENT '从艺年数',
    `career_history` TEXT COMMENT '从艺经历',
    `specialties` VARCHAR(500) DEFAULT NULL COMMENT '擅长领域',
    `representative_works` TEXT COMMENT '代表作品(JSON)',
    `bio` TEXT COMMENT '个人简介',
    `honor` VARCHAR(1000) DEFAULT NULL COMMENT '荣誉称号',
    `audit_status` TINYINT DEFAULT 0 COMMENT '审核状态: 0-待审核 1-通过 2-驳回',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    KEY `idx_heritage_project_id` (`heritage_project_id`),
    KEY `idx_skill_category_id` (`skill_category_id`),
    KEY `idx_audit_status` (`audit_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='师傅档案表';

-- -----------------------------------------------------------
-- 5. 师徒关系表 master_apprentice_relation
-- -----------------------------------------------------------
CREATE TABLE `master_apprentice_relation` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `master_id` BIGINT NOT NULL COMMENT '师傅用户ID',
    `apprentice_id` BIGINT NOT NULL COMMENT '徒弟用户ID',
    `heritage_project_id` BIGINT DEFAULT NULL COMMENT '非遗项目ID',
    `apply_reason` TEXT COMMENT '拜师理由',
    `status` TINYINT DEFAULT 0 COMMENT '状态: 0-待审核 1-已通过 2-已拒绝 3-已解除',
    `apply_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    `approve_time` DATETIME DEFAULT NULL COMMENT '审批时间',
    `dissolve_time` DATETIME DEFAULT NULL COMMENT '解除时间',
    `dissolve_reason` VARCHAR(500) DEFAULT NULL COMMENT '解除原因',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_master_id` (`master_id`),
    KEY `idx_apprentice_id` (`apprentice_id`),
    KEY `idx_status` (`status`),
    UNIQUE KEY `uk_master_apprentice` (`master_id`, `apprentice_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='师徒关系表';

-- -----------------------------------------------------------
-- 6. 教学任务表 teaching_task
-- -----------------------------------------------------------
CREATE TABLE `teaching_task` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `title` VARCHAR(200) NOT NULL COMMENT '任务标题',
    `description` TEXT COMMENT '任务描述',
    `master_id` BIGINT NOT NULL COMMENT '师傅用户ID',
    `heritage_project_id` BIGINT DEFAULT NULL COMMENT '非遗项目ID',
    `skill_category_id` BIGINT DEFAULT NULL COMMENT '技艺类别ID',
    `deadline` DATETIME DEFAULT NULL COMMENT '截止时间',
    `status` TINYINT DEFAULT 0 COMMENT '状态: 0-已发布 1-进行中 2-已完成 3-已关闭',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted_at` DATETIME DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_master_id` (`master_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教学任务表';

-- -----------------------------------------------------------
-- 7. 学习材料表 learning_material
-- -----------------------------------------------------------
CREATE TABLE `learning_material` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `title` VARCHAR(200) NOT NULL COMMENT '材料标题',
    `description` TEXT COMMENT '描述',
    `file_url` VARCHAR(500) NOT NULL COMMENT '文件URL',
    `file_type` VARCHAR(50) DEFAULT NULL COMMENT '文件类型: PDF/IMAGE/VIDEO/DOC',
    `file_size` BIGINT DEFAULT NULL COMMENT '文件大小(字节)',
    `task_id` BIGINT DEFAULT NULL COMMENT '关联教学任务ID',
    `uploader_id` BIGINT NOT NULL COMMENT '上传者用户ID',
    `heritage_project_id` BIGINT DEFAULT NULL COMMENT '非遗项目ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted_at` DATETIME DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_task_id` (`task_id`),
    KEY `idx_uploader_id` (`uploader_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学习材料表';

-- -----------------------------------------------------------
-- 8. 任务提交表 task_submission
-- -----------------------------------------------------------
CREATE TABLE `task_submission` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `task_id` BIGINT NOT NULL COMMENT '教学任务ID',
    `apprentice_id` BIGINT NOT NULL COMMENT '徒弟用户ID',
    `content` TEXT COMMENT '提交内容/学习心得',
    `file_url` VARCHAR(500) DEFAULT NULL COMMENT '附件URL',
    `status` TINYINT DEFAULT 0 COMMENT '状态: 0-已提交 1-已查看 2-已点评',
    `master_comment` TEXT COMMENT '师傅评语',
    `score` INT DEFAULT NULL COMMENT '评分',
    `submit_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    `review_time` DATETIME DEFAULT NULL COMMENT '点评时间',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_task_id` (`task_id`),
    KEY `idx_apprentice_id` (`apprentice_id`),
    UNIQUE KEY `uk_task_apprentice` (`task_id`, `apprentice_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务提交表';

-- -----------------------------------------------------------
-- 9. 作品表 artwork
-- -----------------------------------------------------------
CREATE TABLE `artwork` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `title` VARCHAR(200) NOT NULL COMMENT '作品标题',
    `description` TEXT COMMENT '作品描述',
    `apprentice_id` BIGINT NOT NULL COMMENT '徒弟用户ID',
    `master_id` BIGINT DEFAULT NULL COMMENT '指导师傅用户ID',
    `heritage_project_id` BIGINT DEFAULT NULL COMMENT '非遗项目ID',
    `skill_category_id` BIGINT DEFAULT NULL COMMENT '技艺类别ID',
    `image_urls` TEXT COMMENT '图片URL列表(JSON数组)',
    `video_url` VARCHAR(500) DEFAULT NULL COMMENT '视频URL',
    `status` TINYINT DEFAULT 0 COMMENT '状态: 0-草稿 1-已提交 2-已点评',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted_at` DATETIME DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_apprentice_id` (`apprentice_id`),
    KEY `idx_master_id` (`master_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作品表';

-- -----------------------------------------------------------
-- 10. 作品点评表 artwork_review
-- -----------------------------------------------------------
CREATE TABLE `artwork_review` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `artwork_id` BIGINT NOT NULL COMMENT '作品ID',
    `reviewer_id` BIGINT NOT NULL COMMENT '点评人用户ID(师傅)',
    `content` TEXT NOT NULL COMMENT '点评内容',
    `score` INT DEFAULT NULL COMMENT '评分(1-100)',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_artwork_id` (`artwork_id`),
    KEY `idx_reviewer_id` (`reviewer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作品点评表';

-- -----------------------------------------------------------
-- 11. 成长记录表 growth_record
-- -----------------------------------------------------------
CREATE TABLE `growth_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `apprentice_id` BIGINT NOT NULL COMMENT '徒弟用户ID',
    `record_type` VARCHAR(30) NOT NULL COMMENT '记录类型: TASK_COMPLETE/ARTWORK_SUBMIT/ARTWORK_REVIEWED/COURSE_JOIN/MILESTONE',
    `title` VARCHAR(200) NOT NULL COMMENT '记录标题',
    `description` TEXT COMMENT '记录描述',
    `related_id` BIGINT DEFAULT NULL COMMENT '关联实体ID',
    `related_type` VARCHAR(50) DEFAULT NULL COMMENT '关联实体类型',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_apprentice_id` (`apprentice_id`),
    KEY `idx_record_type` (`record_type`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='成长记录表';

-- -----------------------------------------------------------
-- 12. 活动课程表 activity
-- -----------------------------------------------------------
CREATE TABLE `activity` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `title` VARCHAR(200) NOT NULL COMMENT '活动标题',
    `description` TEXT COMMENT '活动简介',
    `content` TEXT COMMENT '活动详情',
    `cover_url` VARCHAR(500) DEFAULT NULL COMMENT '封面图片',
    `location` VARCHAR(200) DEFAULT NULL COMMENT '活动地点',
    `start_time` DATETIME DEFAULT NULL COMMENT '开始时间',
    `end_time` DATETIME DEFAULT NULL COMMENT '结束时间',
    `max_participants` INT DEFAULT NULL COMMENT '最大参与人数',
    `current_participants` INT DEFAULT 0 COMMENT '当前参与人数',
    `heritage_project_id` BIGINT DEFAULT NULL COMMENT '非遗项目ID',
    `status` TINYINT DEFAULT 0 COMMENT '状态: 0-即将开始 1-进行中 2-已结束 3-已取消',
    `publisher_id` BIGINT NOT NULL COMMENT '发布者用户ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted_at` DATETIME DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`),
    KEY `idx_start_time` (`start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动课程表';

-- -----------------------------------------------------------
-- 13. 活动参与表 activity_participation
-- -----------------------------------------------------------
CREATE TABLE `activity_participation` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `activity_id` BIGINT NOT NULL COMMENT '活动ID',
    `user_id` BIGINT NOT NULL COMMENT '参与者用户ID',
    `status` TINYINT DEFAULT 0 COMMENT '状态: 0-已报名 1-已签到 2-已取消',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_activity_user` (`activity_id`, `user_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动参与表';

-- -----------------------------------------------------------
-- 14. 公告表 announcement
-- -----------------------------------------------------------
CREATE TABLE `announcement` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `title` VARCHAR(200) NOT NULL COMMENT '公告标题',
    `content` TEXT NOT NULL COMMENT '公告内容',
    `publisher_id` BIGINT NOT NULL COMMENT '发布者用户ID',
    `is_top` TINYINT DEFAULT 0 COMMENT '是否置顶: 0-否 1-是',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-草稿 1-已发布',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted_at` DATETIME DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`),
    KEY `idx_is_top` (`is_top`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公告表';

-- -----------------------------------------------------------
-- 15. 审计日志表 audit_log
-- -----------------------------------------------------------
CREATE TABLE `audit_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` BIGINT DEFAULT NULL COMMENT '操作用户ID',
    `username` VARCHAR(50) DEFAULT NULL COMMENT '操作用户名',
    `operation` VARCHAR(50) DEFAULT NULL COMMENT '操作类型',
    `method` VARCHAR(200) DEFAULT NULL COMMENT '请求方法',
    `params` TEXT COMMENT '请求参数',
    `ip` VARCHAR(50) DEFAULT NULL COMMENT '请求IP',
    `result_status` TINYINT DEFAULT NULL COMMENT '结果状态: 0-失败 1-成功',
    `error_msg` TEXT COMMENT '错误信息',
    `duration` BIGINT DEFAULT NULL COMMENT '执行时长(ms)',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_created_at` (`created_at`),
    KEY `idx_operation` (`operation`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审计日志表';
