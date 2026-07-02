package com.devflow.service;

import com.devflow.dto.*;
import java.util.List;

public interface IssueService {
    IssueResponse create(Long projectId, IssueRequest req, Long reporterId);
    IssueResponse update(Long issueId, IssueRequest req);
    void delete(Long issueId);
    IssueResponse getById(Long issueId);
    List<IssueResponse> listByProject(Long projectId, String status, String type, Long assigneeId);
    List<IssueResponse> listBySprint(Long sprintId);
    void transitionStatus(Long issueId, String targetStatus);
    void assign(Long issueId, Long assigneeId);
    // 评论
    void addComment(Long issueId, Long userId, CommentRequest req);
    List<?> getComments(Long issueId);
}
