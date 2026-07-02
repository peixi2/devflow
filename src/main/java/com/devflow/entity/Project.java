package com.devflow.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 项目表
 */
@Entity
@Table(name = "project")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Project extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    /** ACTIVE / ARCHIVED / COMPLETED */
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "ACTIVE";

    /** 项目创建者 */
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;
}
