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
     * 获取用户权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    private Set<String> getUserPermissions(Long userId) {
        Set<String> permissions = new HashSet<>();

        // 给所有用户默认添加基本权限
        permissions.add("auth:info");
        permissions.add("auth:logout");
        permissions.add("auth:profile");
        permissions.add("auth:password");

        // 获取用户信息以判断用户类型
        SysUser user = userService.getById(userId);
        if (user != null) {
            // 根据用户类型添加权限
            Integer userType = user.getUserType();
            if (userType != null) {
                switch (userType) {
                    case 1: // 系统管理员
                        // 添加系统管理权限
                        permissions.add("system:user:list");
                        permissions.add("system:user:view");
                        permissions.add("system:user:add");
                        permissions.add("system:user:edit");
                        permissions.add("system:user:remove");
                        permissions.add("system:user:query");
                        permissions.add("system:user:resetPwd");
                        permissions.add("system:role:list");
                        permissions.add("system:role:view");
                        permissions.add("system:menu:list");
                        permissions.add("system:config:view");

                        // 添加物业管理相关权限
                        permissions.add("property:building:list");
                        permissions.add("property:building:add");
                        permissions.add("property:building:edit");
                        permissions.add("property:building:remove");

                        permissions.add("property:unit:list");
                        permissions.add("property:unit:add");
                        permissions.add("property:unit:edit");
                        permissions.add("property:unit:remove");

                        permissions.add("property:house:list");
                        permissions.add("property:house:add");
                        permissions.add("property:house:edit");
                        permissions.add("property:house:remove");

                        permissions.add("property:owner:list");
                        permissions.add("property:owner:add");
                        permissions.add("property:owner:edit");
                        permissions.add("property:owner:remove");

                        // 添加费用管理权限
                        permissions.add("property:feetype:list");
                        permissions.add("property:feetype:view");
                        permissions.add("property:feetype:add");
                        permissions.add("property:feetype:edit");
                        permissions.add("property:feetype:remove");
                        permissions.add("property:feetype:query");

                        // 添加维修管理权限
                        permissions.add("property:repair:list");
                        permissions.add("property:repair:view");
                        permissions.add("property:repair:add");
                        permissions.add("property:repair:edit");
                        permissions.add("property:repair:delete");
                        permissions.add("property:repair:assign");
                        permissions.add("property:repair:accept");
                        permissions.add("property:repair:handle");
                        permissions.add("property:repair:rate");
                        permissions.add("property:repair:archive"); // 归档权限

                        // 添加投诉管理权限（包含所有权限）
                        permissions.add("property:complaint:list");
                        permissions.add("property:complaint:query");
                        permissions.add("property:complaint:add");
                        permissions.add("property:complaint:edit");
                        permissions.add("property:complaint:remove");
                        permissions.add("property:complaint:assign");
                        permissions.add("property:complaint:handle");
                        permissions.add("property:complaint:rate");
                        permissions.add("property:complaint:my");
                        permissions.add("property:complaint:upload");

                        // 添加公告管理权限（包含所有权限）
                        permissions.add("property:notice:list");
                        permissions.add("property:notice:add");
                        permissions.add("property:notice:edit");
                        permissions.add("property:notice:remove");
                        permissions.add("property:notice:publish");
                        permissions.add("property:notice:view");
                        break;

                    case 2: // 物业管理员
                        permissions.add("property:building:list");
                        permissions.add("property:building:add");
                        permissions.add("property:building:edit");
                        permissions.add("property:building:remove");

                        permissions.add("property:unit:list");
                        permissions.add("property:unit:add");
                        permissions.add("property:unit:edit");
                        permissions.add("property:unit:remove");

                        permissions.add("property:house:list");
                        permissions.add("property:house:add");
                        permissions.add("property:house:edit");
                        permissions.add("property:house:remove");

                        permissions.add("property:owner:list");
                        permissions.add("property:owner:add");
                        permissions.add("property:owner:edit");
                        permissions.add("property:owner:remove");

                        // 添加费用管理权限
                        permissions.add("property:feetype:list");
                        permissions.add("property:feetype:view");
                        permissions.add("property:feetype:add");
                        permissions.add("property:feetype:edit");
                        permissions.add("property:feetype:remove");
                        permissions.add("property:feetype:query");

                        // 添加维修管理权限
                        permissions.add("property:repair:list");
                        permissions.add("property:repair:view");
                        permissions.add("property:repair:add");
                        permissions.add("property:repair:edit");
                        permissions.add("property:repair:delete");
                        permissions.add("property:repair:assign");
                        permissions.add("property:repair:accept");
                        permissions.add("property:repair:handle");
                        permissions.add("property:repair:rate");
                        permissions.add("property:repair:archive"); // 归档权限

                        // 添加投诉管理权限
                        permissions.add("property:complaint:list");
                        permissions.add("property:complaint:query");
                        permissions.add("property:complaint:add");
                        permissions.add("property:complaint:edit");
                        permissions.add("property:complaint:remove");
                        permissions.add("property:complaint:assign");
                        permissions.add("property:complaint:handle");
                        permissions.add("property:complaint:upload");

                        // 添加公告管理权限
                        permissions.add("property:notice:list");
                        permissions.add("property:notice:add");
                        permissions.add("property:notice:edit");
                        permissions.add("property:notice:remove");
                        permissions.add("property:notice:publish");
                        break;

                    case 3: // 业主
                        permissions.add("property:owner:list");
                        permissions.add("property:bill:view");
                        permissions.add("property:bill:pay");
                        permissions.add("property:wallet:view");
                        permissions.add("property:wallet:recharge");
                        permissions.add("property:transaction:list");
                        permissions.add("property:complaint:add");
                        permissions.add("property:complaint:query");
                        permissions.add("property:complaint:my");
                        permissions.add("property:complaint:rate");
                        permissions.add("property:complaint:upload");
                        permissions.add("property:repair:add");
                        permissions.add("property:repair:accept");
                        permissions.add("property:repair:rate");
                        permissions.add("property:notice:view");
                        permissions.add("property:notice:list");
                        permissions.add("portal:view");
                        permissions.add("portal:dashboard:view");
                        break;

                    case 4: // 维修人员
                        permissions.add("property:repair:view");
                        permissions.add("property:repair:handle");
                        permissions.add("property:repair:accept");
                        permissions.add("property:notice:view");
                        permissions.add("property:notice:list");
                        break;
                }
            }
        }

        return permissions;
    }
}