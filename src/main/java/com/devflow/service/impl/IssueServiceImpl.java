package com.devflow.service.impl;

import com.devflow.dto.*;
import com.devflow.entity.*;
import com.devflow.repository.*;
import com.devflow.service.IssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class IssueServiceImpl implements IssueService {

    private final IssueRepository issueRepo;
    private final IssueCommentRepository commentRepo;
    private final NotificationRepository notificationRepo;

    // 合法状态流转规则
    private static final Map<String, List<String>> STATUS_FLOW = Map.of(
        "TODO",        List.of("IN_PROGRESS"),
        "IN_PROGRESS", List.of("IN_REVIEW", "TODO"),
        "IN_REVIEW",   List.of("TESTING", "IN_PROGRESS"),
        "TESTING",     List.of("DONE", "IN_PROGRESS"),
        "DONE",        List.of("TODO")
    );

    @Override
    public IssueResponse create(Long projectId, IssueRequest req, Long reporterId) {
        Issue issue = Issue.builder()
                .projectId(projectId).sprintId(req.getSprintId())
                .title(req.getTitle()).description(req.getDescription())
                .type(req.getType()).priority(req.getPriority())
                .assigneeId(req.getAssigneeId()).reporterId(reporterId)
                .status("TODO").build();
        issue = issueRepo.save(issue);

        // 分配了负责人 → 发通知
        if (req.getAssigneeId() != null) {
            notify(req.getAssigneeId(), "ISSUE_ASSIGNED",
                    "新任务分配: " + issue.getTitle(), "ISSUE", issue.getId());
        }
        return toResponse(issue);
    }

    @Override
    public IssueResponse update(Long issueId, IssueRequest req) {
        Issue i = getIssue(issueId);
        i.setTitle(req.getTitle()); i.setDescription(req.getDescription());
        i.setType(req.getType()); i.setPriority(req.getPriority());
        i.setSprintId(req.getSprintId());
        if (req.getAssigneeId() != null) i.setAssigneeId(req.getAssigneeId());
        return toResponse(issueRepo.save(i));
    }

    @Override
    public void delete(Long issueId) {
        issueRepo.deleteById(issueId);
    }

    @Override
    public IssueResponse getById(Long issueId) {
        return toResponse(getIssue(issueId));
    }

    @Override
    public List<IssueResponse> listByProject(Long projectId, String status,
                                              String type, Long assigneeId) {
        List<Issue> issues;
        if (status != null && type != null) {
            issues = issueRepo.findByProjectIdAndStatusAndType(projectId, status, type);
        } else if (status != null) {
            issues = issueRepo.findByProjectIdAndStatus(projectId, status);
        } else if (assigneeId != null) {
            issues = issueRepo.findByAssigneeId(assigneeId);
        } else {
            issues = issueRepo.findByProjectId(projectId);
        }
        return issues.stream().map(this::toResponse).toList();
    }

    @Override
    public List<IssueResponse> listBySprint(Long sprintId) {
        return issueRepo.findBySprintId(sprintId).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public void transitionStatus(Long issueId, String targetStatus) {
        Issue i = getIssue(issueId);
        List<String> allowed = STATUS_FLOW.getOrDefault(i.getStatus(), List.of());
        if (!allowed.contains(targetStatus)) {
            throw new RuntimeException(
                String.format("状态不能从 %s 流转到 %s，允许: %s", i.getStatus(), targetStatus, allowed));
        }
        i.setStatus(targetStatus);
        issueRepo.save(i);

        // 通知创建人
        notify(i.getReporterId(), "STATUS_CHANGED",
                String.format("[%s] 状态变更为 %s", i.getTitle(), targetStatus),
                "ISSUE", i.getId());
    }

    @Override
    public void assign(Long issueId, Long assigneeId) {
        Issue i = getIssue(issueId);
        i.setAssigneeId(assigneeId);
        issueRepo.save(i);
        notify(assigneeId, "ISSUE_ASSIGNED",
                "你被分配了任务: " + i.getTitle(), "ISSUE", i.getId());
    }

    @Override
    public void addComment(Long issueId, Long userId, CommentRequest req) {
        commentRepo.save(IssueComment.builder()
                .issueId(issueId).userId(userId).content(req.getContent()).build());
        Issue i = getIssue(issueId);
        // 通知Issue创建人（非自己评论时）
        if (!i.getReporterId().equals(userId)) {
            notify(i.getReporterId(), "COMMENTED",
                    "你的Issue有新评论: " + i.getTitle(), "ISSUE", i.getId());
        }
    }

    @Override
    public List<Map<String, Object>> getComments(Long issueId) {
        return commentRepo.findByIssueIdOrderByCreatedAtAsc(issueId).stream()
            .map(c -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("id", c.getId()); m.put("content", c.getContent());
                m.put("userId", c.getUserId()); m.put("createdAt", c.getCreatedAt());
                return m;
            }).toList();
    }

    private Issue getIssue(Long id) {
        return issueRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Issue不存在"));
    }

    private IssueResponse toResponse(Issue i) {
        return IssueResponse.builder()
                .id(i.getId()).projectId(i.getProjectId()).sprintId(i.getSprintId())
                .title(i.getTitle()).description(i.getDescription())
                .type(i.getType()).priority(i.getPriority()).status(i.getStatus())
                .assigneeId(i.getAssigneeId()).reporterId(i.getReporterId())
                .aiTags(i.getAiTags()).aiConfidence(i.getAiConfidence())
                .createdAt(i.getCreatedAt()).updatedAt(i.getUpdatedAt())
                .build();
    }

    private void notify(Long userId, String type, String title, String targetType, Long targetId) {
        notificationRepo.save(Notification.builder()
                .userId(userId).type(type).title(title)
                .targetType(targetType).targetId(targetId).build());
    }
}
