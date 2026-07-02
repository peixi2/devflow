package com.devflow.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/**
 * Sprint/版本迭代表
 */
@Entity
@Table(name = "sprint")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Sprint extends BaseEntity {

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String goal;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    /** PLANNING / ACTIVE / COMPLETED */
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "PLANNING";
}
