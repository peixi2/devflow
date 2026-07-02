package com.devflow.service;

import com.devflow.dto.LoginRequest;
import com.devflow.dto.LoginResponse;
import com.devflow.dto.RegisterRequest;

/**
 * 用户服务接口
 */
public interface UserService {

    /** 注册——返回注册成功的用户ID */
    Long register(RegisterRequest request);

    /** 登录——返回 JWT Token + 用户信息 */
    LoginResponse login(LoginRequest request);
}
