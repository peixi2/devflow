package com.devflow.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 角色表
 */
@Entity
@Table(name = "role")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    /** ROLE_ADMIN / ROLE_PM / ROLE_DEV / ROLE_TESTER */
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(length = 255)
    private String description;
}
