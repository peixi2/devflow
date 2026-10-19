package com.devflow.controller;

import com.devflow.dto.*;
import com.devflow.security.ProjectAccessService;
import com.devflow.service.IssueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects/{projectId}/issues")
@RequiredArgsConstructor
public class IssueController {

    private final IssueService issueService;
    private final ProjectAccessService accessService;

    /** 创建 Issue */
    @PostMapping
    public ApiResponse<IssueResponse> create(@PathVariable Long projectId,
                                             @Valid @RequestBody IssueRequest req,
                                             Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        accessService.requireMember(projectId, userId);
        return ApiResponse.success(issueService.create(projectId, req, userId));
    }

    /** Issue 列表（支持筛选） */
    @GetMapping
    public ApiResponse<List<IssueResponse>> list(
            @PathVariable Long projectId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long assigneeId,
            Authentication auth) {
        accessService.requireMember(projectId, userId(auth));
        return ApiResponse.success(issueService.listByProject(projectId, status, type, assigneeId));
    }

    /** Issue 详情 */
    @GetMapping("/{issueId}")
    public ApiResponse<IssueResponse> getById(@PathVariable Long projectId,
                                              @PathVariable Long issueId,
                                              Authentication auth) {
        accessService.requireIssue(projectId, issueId, userId(auth));
        return ApiResponse.success(issueService.getById(issueId));
    }

    /** 更新 Issue */
    @PutMapping("/{issueId}")
    public ApiResponse<IssueResponse> update(@PathVariable Long projectId,
                                             @PathVariable Long issueId,
                                             @Valid @RequestBody IssueRequest req,
                                             Authentication auth) {
        accessService.requireIssue(projectId, issueId, userId(auth));
        return ApiResponse.success(issueService.update(issueId, req));
    }

    /** 删除 Issue */
    @DeleteMapping("/{issueId}")
    public ApiResponse<Void> delete(@PathVariable Long projectId,
                                    @PathVariable Long issueId,
                                    Authentication auth) {
        accessService.requireIssue(projectId, issueId, userId(auth));
        issueService.delete(issueId);
        return ApiResponse.success(null);
    }

    /** 状态流转: POST /api/projects/1/issues/1/transition?to=IN_PROGRESS */
    @PostMapping("/{issueId}/transition")
    public ApiResponse<Void> transition(@PathVariable Long projectId,
                                        @PathVariable Long issueId,
                                        @RequestParam String to,
                                        Authentication auth) {
        accessService.requireIssue(projectId, issueId, userId(auth));
        issueService.transitionStatus(issueId, to);
        return ApiResponse.success(null);
    }

    /** 分配负责人 */
    @PutMapping("/{issueId}/assign")
    public ApiResponse<Void> assign(@PathVariable Long projectId,
                                    @PathVariable Long issueId,
                                    @RequestBody Map<String, Long> body,
                                    Authentication auth) {
        accessService.requireIssue(projectId, issueId, userId(auth));
        issueService.assign(issueId, body.get("assigneeId"));
        return ApiResponse.success(null);
    }

    /** 添加评论 */
    @PostMapping("/{issueId}/comments")
    public ApiResponse<Void> addComment(@PathVariable Long projectId,
                                        @PathVariable Long issueId,
                                        @Valid @RequestBody CommentRequest req,
                                        Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        accessService.requireIssue(projectId, issueId, userId);
        issueService.addComment(issueId, userId, req);
        return ApiResponse.success(null);
    }

    /** 获取评论 */
    @GetMapping("/{issueId}/comments")
    public ApiResponse<List<?>> getComments(@PathVariable Long projectId,
                                            @PathVariable Long issueId,
                                            Authentication auth) {
        accessService.requireIssue(projectId, issueId, userId(auth));
        return ApiResponse.success(issueService.getComments(issueId));
    }

    private Long userId(Authentication auth) {
        return (Long) auth.getPrincipal();
    }
}
