package com.zhl.transaction.controller.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse<T> {
    public static final String SUCC = "200";
    public static final String FAIL = "400";
    private String status;
    private String errorMsg;
    private T data;

    public ApiResponse(String status, String errorMsg, T data) {
        this.status = status;
        this.errorMsg = errorMsg;
        this.data = data;
    }

    public static <T> ApiResponse<T> successWithData(T data) {
        return new ApiResponse<>(SUCC,null, data);
    }

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(SUCC, null, null);
    }

    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<>(FAIL, message,null);
    }
}
