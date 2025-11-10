package com.hyu.common.exception;

import lombok.Getter;

/**
 * 安全异常
 */
@Getter
public class SecurityException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误提示
     */
    private String message;

    public SecurityException(String message) {
        this.message = message;
        this.code = 401;
    }

    public SecurityException(String message, Integer code) {
        this.message = message;
        this.code = code;
    }

    public SecurityException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.code = 401;
    }
}