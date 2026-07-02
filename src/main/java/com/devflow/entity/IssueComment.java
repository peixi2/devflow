package com.devflow.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Issue评论表
 */
@Entity
@Table(name = "issue_comment")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class IssueComment extends BaseEntity {

    @Column(name = "issue_id", nullable = false)
    private Long issueId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
}
