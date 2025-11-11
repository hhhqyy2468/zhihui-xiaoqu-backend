package com.hyu.controller;

import com.hyu.common.core.domain.AjaxResult;
import com.hyu.common.domain.LoginUser;
import com.hyu.common.utils.SecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 权限测试控制器
 *
 * @author hyu
 */
@RestController
@RequestMapping("/test/permission")
public class TestPermissionController {

    /**
     * 获取当前用户信息和权限
     */
    @GetMapping("/current")
    public AjaxResult getCurrentUserPermissions() {
        try {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser == null) {
                return AjaxResult.error("用户未登录");
            }

            Map<String, Object> result = new HashMap<>();
            result.put("userId", loginUser.getUserId());
            result.put("username", loginUser.getUsername());
            result.put("realName", loginUser.getRealName());
            result.put("userType", loginUser.getUserType());
            result.put("permissions", loginUser.getPermissions());
            result.put("authorities", loginUser.getAuthorities());

            return AjaxResult.success("获取当前用户权限成功", result);
        } catch (Exception e) {
            return AjaxResult.error("获取权限信息失败：" + e.getMessage());
        }
    }

    /**
     * 测试系统管理员权限
     */
    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('system:user:list')")
    public AjaxResult testAdminPermission() {
        return AjaxResult.success("管理员权限验证通过 - 可以访问系统用户管理");
    }

    /**
     * 测试物业管理员权限
     */
    @GetMapping("/property")
    @PreAuthorize("hasAuthority('property:building:list')")
    public AjaxResult testPropertyPermission() {
        return AjaxResult.success("物业管理员权限验证通过 - 可以访问楼栋管理");
    }

    /**
     * 测试业主权限
     */
    @GetMapping("/owner")
    @PreAuthorize("hasAuthority('property:house:view')")
    public AjaxResult testOwnerPermission() {
        return AjaxResult.success("业主权限验证通过 - 可以查看自己的房产");
    }

    /**
     * 测试维修工权限
     */
    @GetMapping("/worker")
    @PreAuthorize("hasAuthority('property:repair:process')")
    public AjaxResult testWorkerPermission() {
        return AjaxResult.success("维修工权限验证通过 - 可以处理维修单");
    }

    /**
     * 测试通用权限
     */
    @GetMapping("/common")
    @PreAuthorize("hasAuthority('auth:profile')")
    public AjaxResult testCommonPermission() {
        return AjaxResult.success("通用权限验证通过 - 所有用户都应该有此权限");
    }

    /**
     * 测试不存在的权限
     */
    @GetMapping("/denied")
    @PreAuthorize("hasAuthority('nonexistent:permission')")
    public AjaxResult testDeniedPermission() {
        return AjaxResult.success("不应该访问到此接口");
    }
}