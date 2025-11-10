package com.hyu.framework.security.service;

import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * 权限服务
 */
@Service
public class PermissionService {

    /**
     * 获取角色数据权限
     */
    public Set<String> getRolePermission(Long userId) {
        // TODO: 实现角色权限查询
        return Set.of();
    }

    /**
     * 获取菜单数据权限
     */
    public Set<String> getMenuPermission(Long userId) {
        // TODO: 实现菜单权限查询
        return Set.of();
    }
}