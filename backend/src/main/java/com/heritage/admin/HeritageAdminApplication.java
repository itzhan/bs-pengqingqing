package com.heritage.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.heritage.admin.mapper")
public class HeritageAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(HeritageAdminApplication.class, args);
    }
}
