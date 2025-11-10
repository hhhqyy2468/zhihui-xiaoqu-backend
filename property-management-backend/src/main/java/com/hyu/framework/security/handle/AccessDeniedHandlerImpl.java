package com.hyu.framework.security.handle;

import com.alibaba.fastjson2.JSON;
import com.hyu.common.constant.HttpStatus;
import com.hyu.common.core.domain.AjaxResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 权限不够处理类
 */
@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        int code = HttpStatus.FORBIDDEN;
        String msg = "权限不足，无法访问系统资源";

        AjaxResult<Void> result = AjaxResult.error(code, msg);
        String json = JSON.toJSONString(result);

        response.setStatus(code);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().print(json);
    }
}