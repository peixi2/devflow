package com.devflow.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Issue-标签关联表
 */
@Entity
@Table(name = "issue_label", uniqueConstraints = @UniqueConstraint(columnNames = {"issue_id", "label_id"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class IssueLabel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "issue_id", nullable = false)
    private Long issueId;

    @Column(name = "label_id", nullable = false)
    private Long labelId;
}
