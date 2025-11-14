package com.hyu.common.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.hyu.common.domain.LoginUser;

/**
 * Security Utility Class
 */
public class SecurityUtils {

    /**
     * Get User ID
     */
    public static Long getUserId() {
        try {
            LoginUser loginUser = getLoginUser();
            if (loginUser != null) {
                return loginUser.getUserId();
            }
        } catch (Exception e) {
            // ignore exception
        }
        return null;
    }

    /**
     * Get Username
     */
    public static String getUsername() {
        try {
            LoginUser loginUser = getLoginUser();
            if (loginUser != null) {
                return loginUser.getUsername();
            }
        } catch (Exception e) {
            // ignore exception
        }
        return null;
    }

    /**
     * Get Login User
     */
    public static LoginUser getLoginUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
                return (LoginUser) authentication.getPrincipal();
            }
        } catch (Exception e) {
            // ignore exception
        }
        return null;
    }

    /**
     * Check if Admin
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