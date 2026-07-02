package com.devflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 统一 API 响应体
 */
@Data
@AllArgsConstructor
public class ApiResponse<T> {
    private int code;      // 200=成功
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "ok", data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}
