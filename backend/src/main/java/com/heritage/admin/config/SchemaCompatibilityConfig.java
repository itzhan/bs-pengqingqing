package com.heritage.admin.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
@Configuration
public class SchemaCompatibilityConfig {

    @Bean
    public CommandLineRunner ensureTeachingTaskApprenticeColumn(JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                String schema = jdbcTemplate.queryForObject("SELECT DATABASE()", String.class);
                if (schema == null || schema.isBlank()) {
                    return;
                }

                Integer columnCount = jdbcTemplate.queryForObject(
                        "SELECT COUNT(1) FROM information_schema.COLUMNS " +
                                "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = 'teaching_task' AND COLUMN_NAME = 'apprentice_id'",
                        Integer.class,
                        schema
                );
                if (columnCount == null || columnCount == 0) {
                    jdbcTemplate.execute(
                            "ALTER TABLE teaching_task " +
                                    "ADD COLUMN apprentice_id BIGINT NULL COMMENT '指定徒弟ID，NULL表示面向全部有效徒弟' AFTER master_id"
                    );
                    log.info("[SchemaCompatibility] 已补齐 teaching_task.apprentice_id 字段");
                }

                Integer indexCount = jdbcTemplate.queryForObject(
                        "SELECT COUNT(1) FROM information_schema.STATISTICS " +
                                "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = 'teaching_task' AND INDEX_NAME = 'idx_apprentice_id'",
                        Integer.class,
                        schema
                );
                if (indexCount == null || indexCount == 0) {
                    jdbcTemplate.execute("ALTER TABLE teaching_task ADD INDEX idx_apprentice_id (apprentice_id)");
                    log.info("[SchemaCompatibility] 已补齐 teaching_task.idx_apprentice_id 索引");
                }
            } catch (Exception e) {
                log.warn("[SchemaCompatibility] 自动修复 teaching_task 表结构失败，请手动执行 sql/upgrade_20260318_task_assignment.sql", e);
            }
        };
    }
}
