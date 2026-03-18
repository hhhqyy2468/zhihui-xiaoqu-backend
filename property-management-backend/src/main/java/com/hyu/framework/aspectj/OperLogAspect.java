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
            int businessType = resolveBusinessType(method, url);

            // 操作人
            String operName = null;
            Long operUserId = null;
            try {
                operName = SecurityUtils.getUsername();
                operUserId = SecurityUtils.getUserId();
            } catch (Exception ignored) {}

            // 登录/登出操作从请求体中提取用户名
            if ((operName == null || "anonymous".equals(operName)) && request != null) {
                try {
                    Object[] args = joinPoint.getArgs();
                    if (args != null && args.length > 0) {
                        String argJson = JSON.toJSONString(args[0]);
                        com.alibaba.fastjson.JSONObject obj = JSON.parseObject(argJson);
                        if (obj != null && obj.containsKey("username")) {
                            operName = obj.getString("username");
                        }
                    }
                } catch (Exception ignored) {}
            }
            if (operName == null) operName = "anonymous";

            // 请求参数（仅记录POST/PUT body，跳过文件上传参数）
            String operParam = "";
            try {
                Object[] args = joinPoint.getArgs();
                if (args != null && args.length > 0) {
                    // 找第一个非文件、非HttpServletRequest/Response的参数
                    Object targetArg = null;
                    boolean hasFile = false;
                    for (Object arg : args) {
                        if (arg instanceof org.springframework.web.multipart.MultipartFile
                                || arg instanceof org.springframework.web.multipart.MultipartFile[]
                                || arg instanceof javax.servlet.http.HttpServletRequest
                                || arg instanceof javax.servlet.http.HttpServletResponse) {
                            hasFile = true;
                            continue;
                        }
                        if (targetArg == null) targetArg = arg;
                    }
                    if (hasFile && targetArg == null) {
                        operParam = "[file]";
                    } else if (targetArg != null) {
                        operParam = JSON.toJSONString(targetArg);
                        if (operParam.length() > 2000) operParam = operParam.substring(0, 2000);
                    }
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

            String title = resolveTitle(url, className);

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

    private String resolveTitle(String url, String className) {
        if (url == null) return className.replace("Controller", "");
        if (url.contains("/auth/login"))    return "用户登录";
        if (url.contains("/auth/logout"))   return "用户登出";
        if (url.contains("/auth/"))         return "认证管理";
        if (url.contains("/property/bill")) return "账单管理";
        if (url.contains("/property/repair")) return "维修工单";
        if (url.contains("/property/notice")) return "公告管理";
        if (url.contains("/property/owner")) return "业主管理";
        if (url.contains("/property/house")) return "房产管理";
        if (url.contains("/property/building")) return "楼栋管理";
        if (url.contains("/property/unit")) return "单元管理";
        if (url.contains("/property/fee-type")) return "费用类型";
        if (url.contains("/parking/space")) return "车位管理";
        if (url.contains("/parking/rental")) return "车位租赁";
        if (url.contains("/system/user")) return "用户管理";
        if (url.contains("/system/role")) return "角色管理";
        if (url.contains("/system/menu")) return "菜单管理";
        if (url.contains("/system/dict")) return "字典管理";
        if (url.contains("/system/log")) return "日志管理";
        if (url.contains("/wallet")) return "钱包管理";
        if (url.contains("/complaint")) return "投诉建议";
        if (url.contains("/workbench")) return "工作台";
        if (url.contains("/portal")) return "业主门户";
        if (url.contains("/common")) return "公共接口";
        return className.replace("Controller", "");
    }

    private int resolveBusinessType(String httpMethod, String url) {
        if (httpMethod == null) return 0;
        // 登录/登出特殊处理
        if (url != null && url.contains("/auth/login"))  return 1;
        if (url != null && url.contains("/auth/logout")) return 0;
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
