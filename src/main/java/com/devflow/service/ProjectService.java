package com.devflow.service;

import com.devflow.dto.ProjectRequest;
import com.devflow.dto.ProjectResponse;
import java.util.List;

public interface ProjectService {
    ProjectResponse create(ProjectRequest request, Long ownerId);
    ProjectResponse update(Long projectId, ProjectRequest request);
    void delete(Long projectId);
    ProjectResponse getById(Long projectId);
    List<ProjectResponse> listByUser(Long userId);
    void addMember(Long projectId, Long userId, String role);
    void removeMember(Long projectId, Long userId);
}
