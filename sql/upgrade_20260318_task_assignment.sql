USE heritage_archive;

SET NAMES utf8mb4;
SET CHARACTER_SET_CLIENT = utf8mb4;
SET CHARACTER_SET_RESULTS = utf8mb4;
SET CHARACTER_SET_CONNECTION = utf8mb4;

ALTER TABLE `teaching_task`
ADD COLUMN `apprentice_id` BIGINT NULL COMMENT '指定徒弟ID，NULL表示面向全部有效徒弟' AFTER `master_id`;

ALTER TABLE `teaching_task`
ADD INDEX `idx_apprentice_id` (`apprentice_id`);
