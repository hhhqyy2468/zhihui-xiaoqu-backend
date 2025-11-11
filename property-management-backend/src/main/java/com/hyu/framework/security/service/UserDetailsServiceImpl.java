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
     * 获取用户权限（暂时返回空权限）
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    private Set<String> getUserPermissions(Long userId) {
        // TODO: 从数据库获取用户权限
        // 这里暂时返回空权限，后续实现角色权限模块时再完善
        Set<String> permissions = new HashSet<>();

        // 给所有用户默认添加基本权限（临时方案）
        permissions.add("auth:info");
        permissions.add("auth:logout");
        permissions.add("auth:profile");
        permissions.add("auth:password");

        return permissions;
    }
}