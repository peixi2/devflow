package com.devflow.controller;

import com.devflow.dto.*;
import com.devflow.security.ProjectAccessService;
import com.devflow.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectAccessService accessService;

    /** 创建项目 */
    @PostMapping
    public ApiResponse<ProjectResponse> create(@Valid @RequestBody ProjectRequest req,
                                               Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(projectService.create(req, userId));
    }

    /** 我的项目列表 */
    @GetMapping
    public ApiResponse<List<ProjectResponse>> list(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(projectService.listByUser(userId));
    }

    /** 项目详情 */
    @GetMapping("/{id}")
    public ApiResponse<ProjectResponse> getById(@PathVariable Long id, Authentication auth) {
        accessService.requireMember(id, userId(auth));
        return ApiResponse.success(projectService.getById(id));
    }

    /** 更新项目 */
    @PutMapping("/{id}")
    public ApiResponse<ProjectResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody ProjectRequest req,
                                               Authentication auth) {
        accessService.requireManager(id, userId(auth));
        return ApiResponse.success(projectService.update(id, req));
    }

    /** 删除项目 */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id, Authentication auth) {
        accessService.requireManager(id, userId(auth));
        projectService.delete(id);
        return ApiResponse.success(null);
    }

    /** 添加成员 */
    @PostMapping("/{id}/members")
    public ApiResponse<Void> addMember(@PathVariable Long id,
                                       @RequestBody Map<String, Object> body,
                                       Authentication auth) {
        accessService.requireManager(id, userId(auth));
        Long userId = Long.valueOf(body.get("userId").toString());
        String role = (String) body.getOrDefault("role", "DEVELOPER");
        projectService.addMember(id, userId, role);
        return ApiResponse.success("成员添加成功", null);
    }

    /** 移除成员 */
    @DeleteMapping("/{id}/members/{userId}")
    public ApiResponse<Void> removeMember(@PathVariable Long id,
                                          @PathVariable Long userId,
                                          Authentication auth) {
        accessService.requireManager(id, userId(auth));
        projectService.removeMember(id, userId);
        return ApiResponse.success(null);
    }

    private Long userId(Authentication auth) {
        return (Long) auth.getPrincipal();
    }
}
