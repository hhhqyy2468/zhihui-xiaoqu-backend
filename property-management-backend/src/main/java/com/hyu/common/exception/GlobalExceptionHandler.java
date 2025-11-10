package com.hyu.common.exception;

import com.hyu.common.constant.HttpStatus;
import com.hyu.common.core.domain.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Set;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 权限校验异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public AjaxResult<Void> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',权限校验失败'{}'", requestURI, e.getMessage());
        return AjaxResult.error(HttpStatus.FORBIDDEN, "没有权限，请联系管理员授权");
    }

    /**
     * 请求方式不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public AjaxResult<Void> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e,
                                                              HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',不支持'{}'请求", requestURI, e.getMethod());
        return AjaxResult.error(HttpStatus.BAD_METHOD, e.getMessage());
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(ServiceException.class)
    public AjaxResult<Void> handleServiceException(ServiceException e, HttpServletRequest request) {
        log.error(e.getMessage(), e);
        Integer code = e.getCode();
        return code != null ? AjaxResult.error(code, e.getMessage()) : AjaxResult.error(e.getMessage());
    }

    /**
     * 请求路径中缺少必需的路径变量
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public AjaxResult<Void> handleMissingServletRequestParameterException(MissingServletRequestParameterException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',缺少必需的请求参数'{}'", requestURI, e.getParameterName());
        return AjaxResult.error(HttpStatus.BAD_REQUEST, String.format("缺少必需的请求参数：%s", e.getParameterName()));
    }

    /**
     * 请求参数类型不匹配
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public AjaxResult<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',请求参数类型不匹配'{}'", requestURI, e.getMessage());
        return AjaxResult.error(HttpStatus.BAD_REQUEST, String.format("请求参数类型不匹配，参数：%s，期望类型：%s", e.getName(), e.getRequiredType().getSimpleName()));
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public AjaxResult<Void> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生未知异常.", requestURI, e);
        return AjaxResult.error(e.getMessage());
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public AjaxResult<Void> handleException(Exception e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生系统异常.", requestURI, e);
        return AjaxResult.error(e.getMessage());
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException.class)
    public AjaxResult<Void> handleBindException(BindException e) {
        log.error(e.getMessage(), e);
        String message = e.getAllErrors().get(0).getDefaultMessage();
        return AjaxResult.error(message);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public AjaxResult<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return AjaxResult.error(message);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public AjaxResult<Void> handleConstraintViolationException(ConstraintViolationException e) {
        log.error(e.getMessage(), e);
        String message = "";
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<?> violation : violations) {
                sb.append(violation.getMessage()).append(";");
            }
            message = sb.toString();
            if (message.endsWith(";")) {
                message = message.substring(0, message.length() - 1);
            }
        }
        return AjaxResult.error(message);
    }

    /**
     * 认证失败异常
     */
    @ExceptionHandler(AuthenticationException.class)
    public AjaxResult<Void> handleAuthenticationException(AuthenticationException e) {
        log.error("认证失败：{}", e.getMessage());
        return AjaxResult.error(HttpStatus.UNAUTHORIZED, "认证失败，请重新登录");
    }

    /**
     * 凭证错误异常
     */
    @ExceptionHandler(BadCredentialsException.class)
    public AjaxResult<Void> handleBadCredentialsException(BadCredentialsException e) {
        log.error("凭证错误：{}", e.getMessage());
        return AjaxResult.error(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
    }

    /**
     * 安全异常
     */
    @ExceptionHandler(SecurityException.class)
    public AjaxResult<Void> handleSecurityException(SecurityException e) {
        log.error("安全异常：{}", e.getMessage());
        return AjaxResult.error(e.getCode(), e.getMessage());
    }

    /**
     * 数据重复异常
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public AjaxResult<Void> handleDuplicateKeyException(DuplicateKeyException e) {
        log.error("数据重复：{}", e.getMessage());
        return AjaxResult.error(HttpStatus.CONFLICT, "数据已存在，请勿重复添加");
    }

    /**
     * 404异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public AjaxResult<Void> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.error("请求路径不存在：{}", e.getRequestURL());
        return AjaxResult.error(HttpStatus.NOT_FOUND, "请求的资源不存在");
    }
}