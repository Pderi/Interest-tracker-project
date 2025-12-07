package com.interest.tracker;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Interest Tracker 应用启动类
 */
@SpringBootApplication
// 暂时注释，等有业务模块后再启用
// @MapperScan(basePackages = "com.interest.tracker.module.*.dal.mysql")
public class InterestTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(InterestTrackerApplication.class, args);
    }

}

