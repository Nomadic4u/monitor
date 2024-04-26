package org.example.entity;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;

import javax.swing.text.html.Option;
import java.util.Optional;

public record RestBean<T>(int code, T data, String message) {
    public static <T> RestBean<T> success(T data) {
        return new RestBean<>(200, data, "请求成功");
    }

    public static <T> RestBean<T> success() {
        return success(null);
    }
    public static <T> RestBean<T> unauthorized(String message) {
        return failure(401, message);
    }

    public static <T> RestBean<T> forbidden(String message) {
        return failure(403, message);
    }

    public static <T> RestBean<T> failure(int code, String message) {
        return new RestBean<>(code, null, message);
    }

    public String asJsonString() {
        return JSONObject.toJSONString(this, JSONWriter.Feature.WriteNulls);
    }

    public static <T> RestBean<T> noPermission() {
        return new RestBean<>()
    }

    // 获取当前请求的ID
    public static long requestId() {
        String reqId = Optional.ofNullable(MDC.get("reqId")).orElse("0");
        return Long.parseLong(reqId);

    }
}
