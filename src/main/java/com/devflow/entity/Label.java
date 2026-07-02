package com.devflow.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 标签表（AI自动打标签 / 手动标签）
 */
@Entity
@Table(name = "label")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Label {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(nullable = false, length = 50)
    private String name;

    /** 颜色 hex，如 #FF6B6B */
    @Column(length = 7)
    private String color;
}
