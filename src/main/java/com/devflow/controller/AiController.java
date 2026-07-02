package com.devflow.controller;

import com.devflow.ai.AiService;
import com.devflow.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * AI 功能控制器
 */
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    /** Issue 自动分类 */
    @PostMapping("/issues/{issueId}/classify")
    public ApiResponse<Map<String, Object>> classify(@PathVariable Long issueId) {
        return ApiResponse.success(aiService.classifyIssue(issueId));
    }

    /** 相似Issue检索 */
    @GetMapping("/issues/{issueId}/similar")
    public ApiResponse<List<Map<String, Object>>> findSimilar(@PathVariable Long issueId) {
        return ApiResponse.success(aiService.findSimilarIssues(issueId));
    }

    /** 每日站报 */
    @GetMapping("/projects/{projectId}/standup")
    public ApiResponse<Map<String, Object>> standup(@PathVariable Long projectId) {
        return ApiResponse.success(aiService.generateStandup(projectId));
    }
}
