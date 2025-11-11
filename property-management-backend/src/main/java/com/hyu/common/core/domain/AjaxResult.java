package com.hyu.common.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 统一响应结果
 *
 * @author hyu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AjaxResult {

    /** 状态码 */
    private Integer code;

    /** 返回消息 */
    private String msg;

    /** 数据对象 */
    private Object data;

    /** 时间戳 */
    private Long timestamp;

    public AjaxResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
        this.timestamp = System.currentTimeMillis();
    }

    public AjaxResult(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 操作成功
     */
    public static AjaxResult success() {
        return new AjaxResult(200, "操作成功");
    }

    /**
     * 操作成功
     */
    public static AjaxResult success(String msg) {
        return new AjaxResult(200, msg);
    }

    /**
     * 操作成功
     */
    public static AjaxResult success(Object data) {
        return new AjaxResult(200, "操作成功", data);
    }

    /**
     * 操作成功
     */
    public static AjaxResult success(String msg, Object data) {
        return new AjaxResult(200, msg, data);
    }

    /**
     * 操作失败
     */
    public static AjaxResult error() {
        return new AjaxResult(500, "操作失败");
    }

    /**
     * 操作失败
     */
    public static AjaxResult error(String msg) {
        return new AjaxResult(500, msg);
    }

    /**
     * 操作失败
     */
    public static AjaxResult error(Integer code, String msg) {
        return new AjaxResult(code, msg);
    }

    /**
     * 参数验证失败
     */
    public static AjaxResult error(Integer code, String msg, Object data) {
        return new AjaxResult(code, msg, data);
    }

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return this.code == 200;
    }
}