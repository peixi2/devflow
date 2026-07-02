package com.devflow.service.impl;

import com.devflow.dto.ProjectRequest;
import com.devflow.dto.ProjectResponse;
import com.devflow.entity.Project;
import com.devflow.entity.ProjectMember;
import com.devflow.repository.ProjectRepository;
import com.devflow.repository.ProjectMemberRepository;
import com.devflow.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepo;
    private final ProjectMemberRepository memberRepo;

    @Override
    public ProjectResponse create(ProjectRequest req, Long ownerId) {
        Project p = Project.builder()
                .name(req.getName())
                .description(req.getDescription())
                .ownerId(ownerId)
                .status("ACTIVE")
                .build();
        p = projectRepo.save(p);
        // 创建者自动成为PM
        memberRepo.save(ProjectMember.builder()
                .projectId(p.getId()).userId(ownerId).role("PM").build());
        return toResponse(p);
    }

    @Override
    public ProjectResponse update(Long projectId, ProjectRequest req) {
        Project p = projectRepo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("项目不存在"));
        p.setName(req.getName());
        p.setDescription(req.getDescription());
        return toResponse(projectRepo.save(p));
    }

    @Override
    public void delete(Long projectId) {
        projectRepo.deleteById(projectId);
    }

    @Override
    public ProjectResponse getById(Long projectId) {
        Project p = projectRepo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("项目不存在"));
        return toResponse(p);
    }

    @Override
    public List<ProjectResponse> listByUser(Long userId) {
        // 查用户创建的项目 + 参与的项目
        List<Long> projectIds = memberRepo.findByUserId(userId).stream()
                .map(ProjectMember::getProjectId).distinct().toList();
        if (projectIds.isEmpty()) {
            return projectRepo.findByOwnerId(userId).stream().map(this::toResponse).toList();
        }
        return projectRepo.findAllById(projectIds).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public void addMember(Long projectId, Long userId, String role) {
        if (memberRepo.findByProjectIdAndUserId(projectId, userId).isPresent()) {
            throw new RuntimeException("用户已是项目成员");
        }
        memberRepo.save(ProjectMember.builder()
                .projectId(projectId).userId(userId).role(role).build());
    }

    @Override
    public void removeMember(Long projectId, Long userId) {
        ProjectMember pm = memberRepo.findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new RuntimeException("成员不存在"));
        memberRepo.delete(pm);
    }

    private ProjectResponse toResponse(Project p) {
        return ProjectResponse.builder()
                .id(p.getId()).name(p.getName()).description(p.getDescription())
                .status(p.getStatus()).ownerId(p.getOwnerId()).createdAt(p.getCreatedAt())
                .build();
    }
}
