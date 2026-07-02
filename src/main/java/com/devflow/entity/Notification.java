package com.devflow.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 通知表（Issue分配提醒、评论通知、AI站报推送）
 */
@Entity
@Table(name = "notification")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Notification extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** ISSUE_ASSIGNED / COMMENTED / MENTIONED / STATUS_CHANGED / AI_STANDUP */
    @Column(nullable = false, length = 30)
    private String type;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_read")
    @Builder.Default
    private Boolean isRead = false;

    /** 关联类型：ISSUE / PROJECT */
    @Column(name = "target_type", length = 30)
    private String targetType;

    /** 关联ID */
    @Column(name = "target_id")
    private Long targetId;
}
