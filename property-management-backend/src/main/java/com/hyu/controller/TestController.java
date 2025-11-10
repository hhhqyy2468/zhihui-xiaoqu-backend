package com.hyu.controller;

import com.hyu.common.core.domain.AjaxResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 */
@RestController
@RequestMapping("/test")
@Tag(name = "测试接口", description = "用于测试系统基础功能")
public class TestController {

    /**
     * 健康检查
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查系统是否正常运行")
    public AjaxResult<Map<String, Object>> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("application", "property-management-backend");
        result.put("version", "1.0.0");
        result.put("timestamp", System.currentTimeMillis());

        return AjaxResult.success("系统运行正常", result);
    }

    /**
     * 测试认证
     */
    @GetMapping("/auth")
    @Operation(summary = "测试认证", description = "测试JWT认证是否正常工作")
    public AjaxResult<String> auth() {
        return AjaxResult.success("认证测试成功", "Hello, authenticated user!");
    }

    /**
     * 测试异常处理
     */
    @GetMapping("/error")
    @Operation(summary = "测试异常处理", description = "测试全局异常处理器")
    public AjaxResult<Void> testError() {
        throw new RuntimeException("这是一个测试异常");
    }
}