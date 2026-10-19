package com.devflow.controller;

import com.devflow.ai.AiService;
import com.devflow.ai.AiRateLimiter;
import com.devflow.dto.ApiResponse;
import com.devflow.security.ProjectAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
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
    private final AiRateLimiter rateLimiter;
    private final ProjectAccessService accessService;

    /** Issue 自动分类 */
    @PostMapping("/issues/{issueId}/classify")
    public ApiResponse<Map<String, Object>> classify(@PathVariable Long issueId,
                                                     Authentication auth) {
        accessService.requireIssueMember(issueId, userId(auth));
        rateLimiter.checkAllowed();
        return ApiResponse.success(aiService.classifyIssue(issueId));
    }

    /** 相似Issue检索 */
    @GetMapping("/issues/{issueId}/similar")
    public ApiResponse<List<Map<String, Object>>> findSimilar(@PathVariable Long issueId,
                                                              Authentication auth) {
        accessService.requireIssueMember(issueId, userId(auth));
        rateLimiter.checkAllowed();
        return ApiResponse.success(aiService.findSimilarIssues(issueId));
    }

    /** 每日站报 */
    @GetMapping("/projects/{projectId}/standup")
    public ApiResponse<Map<String, Object>> standup(@PathVariable Long projectId,
                                                    Authentication auth) {
        accessService.requireMember(projectId, userId(auth));
        rateLimiter.checkAllowed();
        return ApiResponse.success(aiService.generateStandup(projectId));
    }

    private Long userId(Authentication auth) {
        return (Long) auth.getPrincipal();
    }
}
