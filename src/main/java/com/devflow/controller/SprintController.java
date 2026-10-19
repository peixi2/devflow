package com.devflow.controller;

import com.devflow.dto.*;
import com.devflow.entity.Sprint;
import com.devflow.security.ProjectAccessService;
import com.devflow.service.SprintService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/sprints")
@RequiredArgsConstructor
public class SprintController {

    private final SprintService sprintService;
    private final ProjectAccessService accessService;

    /** 创建 Sprint */
    @PostMapping
    public ApiResponse<Sprint> create(@PathVariable Long projectId,
                                      @Valid @RequestBody SprintRequest req,
                                      Authentication auth) {
        accessService.requireMember(projectId, userId(auth));
        return ApiResponse.success(sprintService.create(projectId, req));
    }

    /** Sprint 列表 */
    @GetMapping
    public ApiResponse<List<Sprint>> list(@PathVariable Long projectId, Authentication auth) {
        accessService.requireMember(projectId, userId(auth));
        return ApiResponse.success(sprintService.listByProject(projectId));
    }

    /** Sprint 详情 */
    @GetMapping("/{sprintId}")
    public ApiResponse<Sprint> getById(@PathVariable Long projectId,
                                       @PathVariable Long sprintId,
                                       Authentication auth) {
        accessService.requireSprint(projectId, sprintId, userId(auth));
        return ApiResponse.success(sprintService.getById(sprintId));
    }

    /** 更新 Sprint */
    @PutMapping("/{sprintId}")
    public ApiResponse<Sprint> update(@PathVariable Long projectId,
                                      @PathVariable Long sprintId,
                                      @Valid @RequestBody SprintRequest req,
                                      Authentication auth) {
        accessService.requireSprint(projectId, sprintId, userId(auth));
        return ApiResponse.success(sprintService.update(sprintId, req));
    }

    /** 删除 Sprint */
    @DeleteMapping("/{sprintId}")
    public ApiResponse<Void> delete(@PathVariable Long projectId,
                                    @PathVariable Long sprintId,
                                    Authentication auth) {
        accessService.requireSprint(projectId, sprintId, userId(auth));
        sprintService.delete(sprintId);
        return ApiResponse.success(null);
    }

    /** 开始 Sprint */
    @PutMapping("/{sprintId}/start")
    public ApiResponse<Void> start(@PathVariable Long projectId,
                                   @PathVariable Long sprintId,
                                   Authentication auth) {
        accessService.requireSprint(projectId, sprintId, userId(auth));
        sprintService.startSprint(sprintId);
        return ApiResponse.success(null);
    }

    /** 完成 Sprint */
    @PutMapping("/{sprintId}/complete")
    public ApiResponse<Void> complete(@PathVariable Long projectId,
                                      @PathVariable Long sprintId,
                                      Authentication auth) {
        accessService.requireSprint(projectId, sprintId, userId(auth));
        sprintService.completeSprint(sprintId);
        return ApiResponse.success(null);
    }

    private Long userId(Authentication auth) {
        return (Long) auth.getPrincipal();
    }
}
