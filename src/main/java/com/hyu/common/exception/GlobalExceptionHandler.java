package com.hyu.common.exception;

import com.hyu.common.core.domain.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * @author hyu
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public AjaxResult handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.error("业务异常，请求地址：{}，异常信息：{}", request.getRequestURI(), e.getMessage());
        return AjaxResult.error(e.getCode(), e.getMessage());
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public AjaxResult handleException(Exception e, HttpServletRequest request) {
        String error = "系统异常，请联系管理员";
        log.error("系统异常，请求地址：{}，异常信息：{}", request.getRequestURI(), e.getMessage(), e);
        return AjaxResult.error(error);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException.class)
    public AjaxResult handleBindException(BindException e) {
        log.error(e.getMessage(), e);
        String message = e.getAllErrors().get(0).getDefaultMessage();
        return AjaxResult.error(422, message);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public AjaxResult handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        String message = fieldErrors.stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return AjaxResult.error(422, message);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public AjaxResult handleConstraintViolationException(ConstraintViolationException e) {
        log.error(e.getMessage(), e);
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        return AjaxResult.error(422, message);
    }

    /**
     * 认证失败异常
     */
    @ExceptionHandler(AuthenticationException.class)
    public AjaxResult handleAuthenticationException(AuthenticationException e, HttpServletRequest request) {
        log.error("认证失败，请求地址：{}，异常信息：{}", request.getRequestURI(), e.getMessage());
        return AjaxResult.error(401, "认证失败，请重新登录");
    }

    /**
     * 凭证错误异常
     */
    @ExceptionHandler(BadCredentialsException.class)
    public AjaxResult handleBadCredentialsException(BadCredentialsException e, HttpServletRequest request) {
        log.error("凭证错误，请求地址：{}，异常信息：{}", request.getRequestURI(), e.getMessage());
        return AjaxResult.error(401, "用户名或密码错误");
    }

    /**
     * 权限不足异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public AjaxResult handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        log.error("权限不足，请求地址：{}，异常信息：{}", request.getRequestURI(), e.getMessage());
        return AjaxResult.error(403, "权限不足，无法访问");
    }
}