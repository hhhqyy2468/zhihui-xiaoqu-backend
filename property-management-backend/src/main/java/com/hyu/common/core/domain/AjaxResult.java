package com.hyu.common.core.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果
 */
@Data
public class AjaxResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private int code;

    /**
     * 响应消息
     */
    private String msg;

    /**
     * 响应数据
     */
    private T data;

    public AjaxResult() {
    }

    public AjaxResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 成功响应
     */
    public static <T> AjaxResult<T> success() {
        return success(null);
    }

    /**
     * 成功响应
     */
    public static <T> AjaxResult<T> success(T data) {
        return new AjaxResult<>(200, "操作成功", data);
    }

    /**
     * 成功响应
     */
    public static <T> AjaxResult<T> success(String msg, T data) {
        return new AjaxResult<>(200, msg, data);
    }

    /**
     * 失败响应
     */
    public static <T> AjaxResult<T> error() {
        return error("操作失败");
    }

    /**
     * 失败响应
     */
    public static <T> AjaxResult<T> error(String msg) {
        return error(500, msg);
    }

    /**
     * 失败响应
     */
    public static <T> AjaxResult<T> error(int code, String msg) {
        return new AjaxResult<>(code, msg, null);
    }

    /**
     * 构建响应
     */
    public static <T> AjaxResult<T> build(int code, String msg, T data) {
        return new AjaxResult<>(code, msg, data);
    }

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return this.code == 200;
    }
}