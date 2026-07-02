package com.devflow.service.impl;

import com.devflow.dto.LoginRequest;
import com.devflow.dto.LoginResponse;
import com.devflow.dto.RegisterRequest;
import com.devflow.entity.User;
import com.devflow.repository.UserRepository;
import com.devflow.security.JwtUtils;
import com.devflow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现——注册 + 登录
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public Long register(RegisterRequest request) {
        // 1. 校验用户名唯一
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在: " + request.getUsername());
        }

        // 2. BCrypt 加密密码
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 3. 保存用户
        User user = User.builder()
                .username(request.getUsername())
                .password(encodedPassword)
                .nickname(request.getNickname() != null ? request.getNickname() : request.getUsername())
                .email(request.getEmail())
                .status(1)
                .build();

        user = userRepository.save(user);
        return user.getId();
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        // 1. 查用户
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));

        // 2. 验密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 3. 检查状态
        if (user.getStatus() != 1) {
            throw new RuntimeException("账号已被禁用");
        }

        // 4. 生成 JWT
        String token = jwtUtils.generateToken(user.getId(), user.getUsername());

        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .build();
    }
}
