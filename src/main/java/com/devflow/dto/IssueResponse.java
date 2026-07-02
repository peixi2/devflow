package com.devflow.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Builder
public class IssueResponse {
    private Long id;
    private Long projectId;
    private Long sprintId;
    private String title;
    private String description;
    private String type;
    private String priority;
    private String status;
    private Long assigneeId;
    private Long reporterId;
    private String aiTags;
    private Float aiConfidence;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
