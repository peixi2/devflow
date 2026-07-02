package com.devflow.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 健康检查接口——验证项目能启动
 */
@RestController
public class HealthController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "ok",
                "app", "DevFlow",
                "java", System.getProperty("java.version"),
                "time", LocalDateTime.now().toString()
        );
    }
}
