package com.hyu.framework.security.service;

import com.hyu.common.domain.LoginUser;
import com.hyu.common.exception.BusinessException;
import com.hyu.system.domain.SysUser;
import com.hyu.system.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 用户验证处理
 *
 * @author hyu
 */
@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private ISysUserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userService.selectUserByUsername(username);
        if (user == null) {
            log.info("登录用户：{} 不存在.", username);
            throw new UsernameNotFoundException("登录用户：" + username + " 不存在");
        } else if (user.getStatus() == 0) {
            log.info("登录用户：{} 已被停用.", username);
            throw new BusinessException("对不起，您的账号：" + username + " 已被禁用", 1002);
        }

        // 获取用户权限（这里暂时返回空权限，后续可以从数据库获取）
        Set<String> permissions = getUserPermissions(user.getUserId());

        return createLoginUser(user, permissions);
    }

    /**
     * 创建登录用户
     *
     * @param user        用户信息
     * @param permissions 权限列表
     * @return 登录用户
     */
    public LoginUser createLoginUser(SysUser user, Set<String> permissions) {
        return new LoginUser(user, permissions);
    }

    /**
     * 获取用户权限（根据用户类型分配不同权限）
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    private Set<String> getUserPermissions(Long userId) {
        Set<String> permissions = new HashSet<>();

        // 首先获取用户信息以确定用户类型
        SysUser user = userService.getById(userId);
        if (user == null) {
            return permissions;
        }

        // 根据用户类型分配不同权限
        Integer userType = user.getUserType();

        // 基本权限（所有用户都有）
        permissions.add("auth:info");
        permissions.add("auth:logout");
        permissions.add("auth:profile");
        permissions.add("auth:password");

        switch (userType) {
            case 1: // 系统管理员
                // 系统管理权限
                permissions.add("system:user:list");
                permissions.add("system:user:add");
                permissions.add("system:user:edit");
                permissions.add("system:user:remove");
                permissions.add("system:role:list");
                permissions.add("system:role:add");
                permissions.add("system:role:edit");
                permissions.add("system:role:remove");
                permissions.add("system:menu:list");
                permissions.add("system:menu:add");
                permissions.add("system:menu:edit");
                permissions.add("system:menu:remove");
                permissions.add("system:log:list");

                // 物业管理权限（管理员也有所有权限）
                permissions.add("property:building:list");
                permissions.add("property:building:add");
                permissions.add("property:building:edit");
                permissions.add("property:building:delete");
                permissions.add("property:house:list");
                permissions.add("property:house:add");
                permissions.add("property:house:edit");
                permissions.add("property:house:delete");
                permissions.add("property:unit:list");
                permissions.add("property:unit:add");
                permissions.add("property:unit:edit");
                permissions.add("property:unit:delete");
                permissions.add("property:owner:list");
                permissions.add("property:owner:add");
                permissions.add("property:owner:edit");
                permissions.add("property:owner:delete");
                permissions.add("property:bill:list");
                permissions.add("property:bill:add");
                permissions.add("property:bill:edit");
                permissions.add("property:bill:delete");
                permissions.add("property:bill:generate");
                permissions.add("property:bill:overdue");
                permissions.add("property:repair:list");
                permissions.add("property:repair:add");
                permissions.add("property:repair:edit");
                permissions.add("property:repair:delete");
                permissions.add("property:repair:assign");
                permissions.add("property:repair:process");
                permissions.add("property:notice:list");
                permissions.add("property:notice:add");
                permissions.add("property:notice:edit");
                permissions.add("property:notice:delete");
                permissions.add("property:wallet:list");
                permissions.add("property:wallet:add");
                permissions.add("property:wallet:edit");
                permissions.add("property:wallet:freeze");
                permissions.add("property:wallet:unfreeze");
                permissions.add("property:wallet:deduct");
                permissions.add("property:feetype:list");
                permissions.add("property:feetype:add");
                permissions.add("property:feetype:edit");
                permissions.add("property:feetype:remove");
                permissions.add("workbench:stats:view");
                permissions.add("common:upload");
                break;

            case 2: // 物业管理员
                // 物业管理权限
                permissions.add("property:building:list");
                permissions.add("property:building:add");
                permissions.add("property:building:edit");
                permissions.add("property:house:list");
                permissions.add("property:house:add");
                permissions.add("property:house:edit");
                permissions.add("property:unit:list");
                permissions.add("property:unit:add");
                permissions.add("property:unit:edit");
                permissions.add("property:owner:list");
                permissions.add("property:owner:add");
                permissions.add("property:owner:edit");
                permissions.add("property:bill:list");
                permissions.add("property:bill:add");
                permissions.add("property:bill:edit");
                permissions.add("property:bill:generate");
                permissions.add("property:bill:overdue");
                permissions.add("property:repair:list");
                permissions.add("property:repair:add");
                permissions.add("property:repair:edit");
                permissions.add("property:repair:assign");
                permissions.add("property:repair:process");
                permissions.add("property:notice:list");
                permissions.add("property:notice:add");
                permissions.add("property:notice:edit");
                permissions.add("property:wallet:list");
                permissions.add("property:wallet:freeze");
                permissions.add("property:wallet:unfreeze");
                permissions.add("property:feetype:list");
                permissions.add("property:feetype:add");
                permissions.add("property:feetype:edit");
                permissions.add("workbench:stats:view");
                permissions.add("common:upload");
                break;

            case 3: // 业主
                // 业主权限
                permissions.add("property:house:view"); // 只能查看自己的房产
                permissions.add("property:bill:view");  // 只能查看自己的账单
                permissions.add("property:bill:pay");   // 支付账单
                permissions.add("property:repair:add");  // 提交报修
                permissions.add("property:repair:view"); // 查看自己的报修
                permissions.add("property:notice:view"); // 查看通知
                permissions.add("property:notice:read"); // 标记通知已读
                permissions.add("property:wallet:view"); // 查看自己的钱包
                permissions.add("property:wallet:recharge"); // 钱包充值
                break;

            case 4: // 维修工
                // 维修工权限
                permissions.add("property:repair:list"); // 查看分配给自己的维修单
                permissions.add("property:repair:process"); // 处理维修单
                permissions.add("property:repair:view");   // 查看维修单详情
                permissions.add("auth:profile");           // 个人资料
                break;

            default:
                // 默认权限（只有基本权限）
                break;
        }

        return permissions;
    }
}