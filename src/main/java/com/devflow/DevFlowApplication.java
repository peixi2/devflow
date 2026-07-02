package com.devflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * DevFlow - AI驱动的软件项目管理系统
 *
 * 技术栈：Java 21 + Spring Boot 3.2 + Spring AI + PostgreSQL + Redis + RabbitMQ
 */
@SpringBootApplication
public class DevFlowApplication {
    public static void main(String[] args) {
        SpringApplication.run(DevFlowApplication.class, args);
    }
}
