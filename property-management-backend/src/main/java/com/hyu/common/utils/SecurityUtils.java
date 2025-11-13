package com.hyu.common.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.hyu.common.domain.LoginUser;

/**
 * 安全工具类
 */
public class SecurityUtils {

    /**
     * 获取用户ID
     */
    public static Long getUserId() {
        try {
            LoginUser loginUser = getLoginUser();
            if (loginUser != null) {
                return loginUser.getUserId();
            }
        } catch (Exception e) {
            // 忽略异常
        }
        return null;
    }

    /**
     * 获取用户名
     */
    public static String getUsername() {
        try {
            LoginUser loginUser = getLoginUser();
            if (loginUser != null) {
                return loginUser.getUsername();
            }
        } catch (Exception e) {
            // 忽略异常
        }
        return null;
    }

    /**
     * 获取登录用户
     */
    public static LoginUser getLoginUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
                return (LoginUser) authentication.getPrincipal();
            }
        } catch (Exception e) {
            // 忽略异常
        }
        return null;
    }

    /**
     * 判断是否为系统管理员
     */
    public static boolean isAdmin() {
        try {
            LoginUser loginUser = getLoginUser();
            if (loginUser != null) {
                return loginUser.getUserType() != null && loginUser.getUserType() == 1;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
