package com.devflow.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 用户表
 */
@Entity
@Table(name = "`user`")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(length = 50)
    private String nickname;

    @Column(length = 100)
    private String email;

    @Column(length = 255)
    private String avatar;

    /** 0-禁用 1-启用 */
    @Column(nullable = false)
    @Builder.Default
    private Integer status = 1;
}
