package com.hyu.framework.security.handle;

import com.hyu.common.utils.SecurityUtils;
import com.hyu.common.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * 自定义权限实现，ss取自SpringSecurity首字母
 *
 * @author hyu
 */
@Service("ss")
public class PermissionService {

    /**
     * 验证用户是否具备某权限
     *
     * @param permission 权限字符串
     * @return 用户是否具备某权限
     */
    public boolean hasPermi(String permission) {
        if (StringUtils.isEmpty(permission)) {
            return false;
        }

        // 系统管理员拥有所有权限
        if (SecurityUtils.isAdmin()) {
            return true;
        }

        com.hyu.common.domain.LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser != null && loginUser.getUserType() != null) {
            String perm = StringUtils.trim(permission);
            int userType = loginUser.getUserType();
            // 物业管理员（userType=2）对物业相关权限放行
            if (userType == 2 && perm != null && (
                perm.startsWith("property:") ||
                perm.startsWith("finance:") ||
                perm.startsWith("service:") ||
                perm.startsWith("parking:") ||
                perm.startsWith("notice:")
            )) {
                return true;
            }
            // 业主（userType=3）对自身相关接口放行
            if (userType == 3 && perm != null && (
                perm.equals("property:owner:list") ||
                perm.startsWith("finance:wallet:") ||
                perm.startsWith("finance:bill:") ||
                perm.startsWith("notice:")
            )) {
                return true;
            }
            // 维修人员（userType=4）对维修工单相关权限放行
            if (userType == 4 && perm != null && perm.startsWith("property:repair:")) {
                return true;
            }
        }

        Set<String> permissions = SecurityUtils.getLoginUser().getPermissions();
        if (StringUtils.isEmpty(permissions)) {
            return false;
        }
        return permissions.contains(StringUtils.trim(permission));
    }

    /**
     * 验证用户是否不含某权限
     *
     * @param permission 权限字符串
     * @return 用户是否不含某权限
     */
    public boolean lacksPermi(String permission) {
        return !hasPermi(permission);
    }

    /**
     * 验证用户是否具有以下任意一个权限
     *
     * @param permissions 以逗号分隔的权限列表
     * @return 用户是否具有以下任意一个权限
     */
    public boolean hasAnyPermi(String permissions) {
        if (StringUtils.isEmpty(permissions)) {
            return false;
        }
        Set<String> authorities = SecurityUtils.getLoginUser().getPermissions();
        for (String permission : permissions.split(",")) {
            if (permission != null && hasPermi(permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 验证用户是否具有以下所有权限
     *
     * @param permissions 以逗号分隔的权限列表
     * @return 用户是否具有以下所有权限
     */
    public boolean hasAllPermi(String permissions) {
        if (StringUtils.isEmpty(permissions)) {
            return false;
        }
        Set<String> authorities = SecurityUtils.getLoginUser().getPermissions();
        for (String permission : permissions.split(",")) {
            if (!hasPermi(permission)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是否为管理员
     *
     * @return 用户是否为管理员
     */
    public boolean isAdmin() {
        return SecurityUtils.isAdmin();
    }
}