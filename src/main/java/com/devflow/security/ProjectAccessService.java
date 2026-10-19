package com.devflow.security;

import com.devflow.entity.Issue;
import com.devflow.entity.Project;
import com.devflow.entity.ProjectMember;
import com.devflow.entity.Sprint;
import com.devflow.repository.IssueRepository;
import com.devflow.repository.ProjectMemberRepository;
import com.devflow.repository.ProjectRepository;
import com.devflow.repository.SprintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectAccessService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository memberRepository;
    private final IssueRepository issueRepository;
    private final SprintRepository sprintRepository;

    public void requireMember(Long projectId, Long userId) {
        Project project = getProject(projectId);
        if (!project.getOwnerId().equals(userId)
                && memberRepository.findByProjectIdAndUserId(projectId, userId).isEmpty()) {
            throw new AccessDeniedException("无权访问该项目");
        }
    }

    public void requireManager(Long projectId, Long userId) {
        Project project = getProject(projectId);
        if (project.getOwnerId().equals(userId)) {
            return;
        }
        boolean manager = memberRepository.findByProjectIdAndUserId(projectId, userId)
                .map(ProjectMember::getRole)
                .filter("PM"::equals)
                .isPresent();
        if (!manager) {
            throw new AccessDeniedException("仅项目负责人可以执行此操作");
        }
    }

    public void requireIssue(Long projectId, Long issueId, Long userId) {
        requireMember(projectId, userId);
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue不存在"));
        if (!issue.getProjectId().equals(projectId)) {
            throw new AccessDeniedException("Issue不属于该项目");
        }
    }

    public void requireSprint(Long projectId, Long sprintId, Long userId) {
        requireMember(projectId, userId);
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new RuntimeException("Sprint不存在"));
        if (!sprint.getProjectId().equals(projectId)) {
            throw new AccessDeniedException("Sprint不属于该项目");
        }
    }

    public void requireIssueMember(Long issueId, Long userId) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue不存在"));
        requireMember(issue.getProjectId(), userId);
    }

    private Project getProject(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("项目不存在"));
    }
}
