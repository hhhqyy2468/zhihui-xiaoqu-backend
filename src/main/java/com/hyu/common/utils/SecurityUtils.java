package com.hyu.common.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.hyu.common.domain.LoginUser;

/**
 * 安全服务工具类
 *
 * @author hyu
 */
public class SecurityUtils {

    /**
     * 获取用户ID
     **/
    public static Long getUserId() {
        try {
            return getLoginUser().getUserId();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取用户名
     **/
    public static String getUsername() {
        try {
            return getLoginUser().getUsername();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取用户key
     */
    public static String getUserKey() {
        try {
            return getLoginUser().getUserKey();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取登录用户信息
     */
    public static LoginUser getLoginUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
                return (LoginUser) authentication.getPrincipal();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 验证用户是否登录
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }

    /**
     * 验证用户是否具有某权限
     */
    public static boolean hasPermi(String permission) {
        return getLoginUser() != null && getLoginUser().getPermissions().contains(permission);
    }
}