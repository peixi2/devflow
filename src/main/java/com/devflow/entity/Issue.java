package com.devflow.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Issue/任务表——核心业务实体
 */
@Entity
@Table(name = "issue")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Issue extends BaseEntity {

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "sprint_id")
    private Long sprintId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    /** BUG / FEATURE / TASK / IMPROVEMENT */
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String type = "TASK";

    /** HIGHEST / HIGH / MEDIUM / LOW / LOWEST */
    @Column(nullable = false, length = 10)
    @Builder.Default
    private String priority = "MEDIUM";

    /** TODO / IN_PROGRESS / IN_REVIEW / TESTING / DONE */
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "TODO";

    /** 负责人 */
    @Column(name = "assignee_id")
    private Long assigneeId;

    /** 创建人 */
    @Column(name = "reporter_id", nullable = false)
    private Long reporterId;

    // ========== 🤖 AI 字段 ==========

    /** AI 自动分类的标签（JSON数组字符串） */
    @Column(name = "ai_tags", length = 500)
    private String aiTags;

    /** AI 分类置信度 0.0~1.0 */
    @Column(name = "ai_confidence")
    private Float aiConfidence;
}
