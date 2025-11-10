package com.hyu.common.exception;

import lombok.Getter;

/**
 * 业务异常
 *
 * @author hyu
 */
@Getter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误消息
     */
    private String message;

    public BusinessException() {
        super();
    }

    public BusinessException(String message) {
        super(message);
        this.message = message;
        this.code = 500;
    }

    public BusinessException(String message, Integer code) {
        super(message);
        this.message = message;
        this.code = code;
    }

    public BusinessException(String message, Throwable e) {
        super(message, e);
        this.message = message;
        this.code = 500;
    }

    public BusinessException(String message, Integer code, Throwable e) {
        super(message, e);
        this.message = message;
        this.code = code;
    }
}