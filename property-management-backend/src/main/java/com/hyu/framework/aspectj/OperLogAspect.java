package com.hyu.framework.aspectj;

import com.alibaba.fastjson.JSON;
import com.hyu.common.utils.SecurityUtils;
import com.hyu.property.service.ISysOperLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 操作日志AOP切面 - 自动记录所有Controller的增删改操作
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperLogAspect {

    private final ISysOperLogService operLogService;

    /** 记录开始时间 */
    private final ThreadLocal<Long> startTime = new ThreadLocal<>();

    /**
     * 拦截所有property和system包下Controller的POST/PUT/DELETE方法
     */
    @Pointcut("execution(* com.hyu..controller..*(..)) && "
            + "(@annotation(org.springframework.web.bind.annotation.PostMapping) "
            + "|| @annotation(org.springframework.web.bind.annotation.PutMapping) "
            + "|| @annotation(org.springframework.web.bind.annotation.DeleteMapping))")
    public void operLogPointcut() {}

    @Before("operLogPointcut()")
    public void doBefore(JoinPoint joinPoint) {
        startTime.set(System.currentTimeMillis());
    }

    @AfterReturning(pointcut = "operLogPointcut()", returning = "result")
    public void doAfterReturning(JoinPoint joinPoint, Object result) {
        recordLog(joinPoint, result, null);
    }

    @AfterThrowing(pointcut = "operLogPointcut()", throwing = "ex")
    public void doAfterThrowing(JoinPoint joinPoint, Exception ex) {
        recordLog(joinPoint, null, ex);
    }

    private void recordLog(JoinPoint joinPoint, Object result, Exception ex) {
        try {
            long cost = System.currentTimeMillis() - startTime.get();
            startTime.remove();

            HttpServletRequest request = getRequest();
            String url = request != null ? request.getRequestURI() : "";
            String method = request != null ? request.getMethod() : "";
            String ip = request != null ? getClientIp(request) : "";

            // 方法签名
            MethodSignature sig = (MethodSignature) joinPoint.getSignature();
            String className = joinPoint.getTarget().getClass().getSimpleName();
            String methodName = sig.getName();

            // 业务类型
            int businessType = resolveBusinessType(method);

            // 操作人
            String operName = "anonymous";
            Long operUserId = null;
            try {
                operName = SecurityUtils.getUsername();
                operUserId = SecurityUtils.getUserId();
            } catch (Exception ignored) {}

            // 请求参数（仅记录POST/PUT body）
            String operParam = "";
            try {
                Object[] args = joinPoint.getArgs();
                if (args != null && args.length > 0) {
                    operParam = JSON.toJSONString(args[0]);
                    if (operParam.length() > 2000) operParam = operParam.substring(0, 2000);
                }
            } catch (Exception ignored) {}

            // 返回结果
            String jsonResult = "";
            try {
                if (result != null) {
                    jsonResult = JSON.toJSONString(result);
                    if (jsonResult.length() > 2000) jsonResult = jsonResult.substring(0, 2000);
                }
            } catch (Exception ignored) {}

            int status = ex == null ? 0 : 1;
            String errorMsg = ex != null ? ex.getMessage() : null;
            if (errorMsg != null && errorMsg.length() > 500) errorMsg = errorMsg.substring(0, 500);

            String title = className.replace("Controller", "");

            operLogService.recordOperLog(
                    title, businessType,
                    className + "." + methodName, method,
                    1, operName, operUserId,
                    url, ip, "",
                    operParam, jsonResult,
                    status, errorMsg
            );
        } catch (Exception e) {
            log.error("记录操作日志失败", e);
        }
    }

    private int resolveBusinessType(String httpMethod) {
        if (httpMethod == null) return 0;
        switch (httpMethod.toUpperCase()) {
            case "POST":   return 1; // 新增
            case "PUT":    return 2; // 修改
            case "DELETE": return 3; // 删除
            default:       return 0;
        }
    }

    private HttpServletRequest getRequest() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attrs != null ? attrs.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
