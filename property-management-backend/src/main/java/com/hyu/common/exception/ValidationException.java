package com.hyu.common.exception;

/**
 * 参数校验异常
 */
public class ValidationException extends ServiceException {

    private static final long serialVersionUID = 1L;

    public ValidationException(String message) {
        super(message, 400);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, 400);
        this.initCause(cause);
    }
}