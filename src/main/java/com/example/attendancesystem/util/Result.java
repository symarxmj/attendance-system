package com.example.attendancesystem.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;

    //成功响应：
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    //失败响应：
    public static <T> Result<T> error(String msg) {
        return new Result<>(500, msg, null);
    }
}
