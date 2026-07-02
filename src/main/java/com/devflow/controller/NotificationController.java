package com.devflow.controller;

import com.devflow.dto.ApiResponse;
import com.devflow.entity.Notification;
import com.devflow.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepo;

    /** 我的通知列表 */
    @GetMapping
    public ApiResponse<List<Notification>> list(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(notificationRepo.findByUserIdOrderByCreatedAtDesc(userId));
    }

    /** 标记已读 */
    @PutMapping("/{id}/read")
    public ApiResponse<Void> markRead(@PathVariable Long id) {
        Notification n = notificationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("通知不存在"));
        n.setIsRead(true);
        notificationRepo.save(n);
        return ApiResponse.success(null);
    }

    /** 未读数量 */
    @GetMapping("/unread-count")
    public ApiResponse<Integer> unreadCount(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(notificationRepo.findByUserIdAndIsReadFalse(userId).size());
    }
}
